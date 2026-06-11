import os
from datetime import datetime
from flask import jsonify, request
from werkzeug.utils import secure_filename
from models.users import users

# 配置上传文件夹
UPLOAD_FOLDER = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'static', 'uploads')
AVATAR_FOLDER = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'static', 'avatars')
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif', 'webp'}
MAX_FILE_SIZE = 16 * 1024 * 1024  # 16MB

# 确保上传文件夹存在
os.makedirs(UPLOAD_FOLDER, exist_ok=True)
os.makedirs(AVATAR_FOLDER, exist_ok=True)


def allowed_file(filename):
    """检查文件扩展名是否允许"""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


def register_upload_routes(app):
    """注册文件上传相关的路由"""
    
    @app.route('/api/upload', methods=['POST'])
    def upload_file():
        """上传单个文件"""
        try:
            # 检查是否有文件
            if 'file' not in request.files:
                return jsonify({'error': '没有上传文件'}), 400
            
            file = request.files['file']
            
            # 检查文件名是否为空
            if file.filename == '':
                return jsonify({'error': '未选择文件'}), 400
            
            # 检查文件类型
            if not allowed_file(file.filename):
                return jsonify({'error': '不支持的文件类型，仅支持: png, jpg, jpeg, gif, webp'}), 400
            
            # 检查文件大小
            file.seek(0, os.SEEK_END)
            file_size = file.tell()
            file.seek(0)
            
            if file_size > MAX_FILE_SIZE:
                return jsonify({'error': f'文件大小超过限制（最大{MAX_FILE_SIZE//1024//1024}MB）'}), 400
            
            # 生成安全的文件名
            filename = secure_filename(file.filename)
            
            # 添加时间戳避免文件名冲突
            from datetime import datetime
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            name, ext = os.path.splitext(filename)
            filename = f"{name}_{timestamp}{ext}"
            
            # 保存文件
            filepath = os.path.join(UPLOAD_FOLDER, filename)
            file.save(filepath)
            
            # 创建一个测试文件，验证代码执行到这里
            test_file_path = os.path.join(UPLOAD_FOLDER, 'test_upload.txt')
            with open(test_file_path, 'w') as f:
                f.write(f'Test upload at {datetime.now()}\n')
                f.write(f'File saved to: {filepath}\n')
                f.write(f'File size: {os.path.getsize(filepath)}\n')
            
            # 返回文件URL
            file_url = f'/static/uploads/{filename}'
            
            return jsonify({
                'success': True,
                'file_url': file_url,
                'filename': filename,
                'file_size': file_size
            }), 201
            
        except Exception as e:
            return jsonify({'error': '文件上传失败', 'details': str(e)}), 500
    
    @app.route('/api/upload/multiple', methods=['POST'])
    def upload_multiple_files():
        """上传多个文件"""
        try:
            # 检查是否有文件
            if 'files' not in request.files:
                return jsonify({'error': '没有上传文件'}), 400
            
            files = request.files.getlist('files')
            
            if not files or len(files) == 0:
                return jsonify({'error': '未选择文件'}), 400
            
            uploaded_files = []
            errors = []
            
            from datetime import datetime
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            
            for idx, file in enumerate(files):
                try:
                    # 检查文件名是否为空
                    if file.filename == '':
                        errors.append(f'文件{idx+1}: 未选择文件')
                        continue
                    
                    # 检查文件类型
                    if not allowed_file(file.filename):
                        errors.append(f'文件{idx+1}: 不支持的文件类型')
                        continue
                    
                    # 检查文件大小
                    file.seek(0, os.SEEK_END)
                    file_size = file.tell()
                    file.seek(0)
                    
                    if file_size > MAX_FILE_SIZE:
                        errors.append(f'文件{idx+1}: 文件大小超过限制')
                        continue
                    
                    # 生成安全的文件名
                    filename = secure_filename(file.filename)
                    name, ext = os.path.splitext(filename)
                    filename = f"{name}_{timestamp}_{idx+1}{ext}"
                    
                    # 保存文件
                    filepath = os.path.join(UPLOAD_FOLDER, filename)
                    file.save(filepath)
                    
                    # 添加到成功列表
                    uploaded_files.append({
                        'file_url': f'/static/uploads/{filename}',
                        'filename': filename,
                        'file_size': file_size
                    })
                    
                except Exception as e:
                    errors.append(f'文件{idx+1}: 上传失败 - {str(e)}')
            
            return jsonify({
                'success': True,
                'uploaded_files': uploaded_files,
                'total': len(uploaded_files),
                'errors': errors
            }), 201
            
        except Exception as e:
            return jsonify({'error': '文件上传失败', 'details': str(e)}), 500
    
    @app.route('/api/upload/avatar', methods=['POST'])
    def upload_avatar():
        """上传用户头像"""
        try:
            # 检查是否有文件
            if 'file' not in request.files:
                return jsonify({'error': '没有上传文件'}), 400
            
            file = request.files['file']
            
            # 检查文件名是否为空
            if file.filename == '':
                return jsonify({'error': '未选择文件'}), 400
            
            # 检查文件类型
            if not allowed_file(file.filename):
                return jsonify({'error': '不支持的文件类型，仅支持: png, jpg, jpeg, gif, webp'}), 400
            
            # 检查文件大小（头像限制为5MB）
            file.seek(0, os.SEEK_END)
            file_size = file.tell()
            file.seek(0)
            
            MAX_AVATAR_SIZE = 5 * 1024 * 1024  # 5MB
            if file_size > MAX_AVATAR_SIZE:
                return jsonify({'error': f'文件大小超过限制（最大{MAX_AVATAR_SIZE//1024//1024}MB）'}), 400
            
            # 获取用户ID
            user_id = request.form.get('user_id')
            if not user_id:
                return jsonify({'error': '缺少用户ID'}), 400
            
            # 检查用户是否存在
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 400
            
            # 生成安全的文件名
            filename = secure_filename(file.filename)
            
            # 添加时间戳避免文件名冲突
            from datetime import datetime
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            name, ext = os.path.splitext(filename)
            filename = f"avatar_{timestamp}{ext}"
            
            # 保存文件到头像文件夹
            filepath = os.path.join(AVATAR_FOLDER, filename)
            file.save(filepath)
            
            # 生成文件URL
            file_url = f'/static/avatars/{filename}'
            
            # 更新用户的avatar_url
            users.update(avatar_url=file_url).where(users.id == user_id).execute()
            
            return jsonify({
                'success': True,
                'file_url': file_url,
                'filename': filename,
                'file_size': file_size
            }), 201
            
        except Exception as e:
            return jsonify({'error': '头像上传失败', 'details': str(e)}), 500
    
    @app.route('/api/upload/pet-avatar', methods=['POST'])
    def upload_pet_avatar():
        """上传宠物头像（不更新用户头像）"""
        try:
            # 检查是否有文件
            if 'file' not in request.files:
                return jsonify({'error': '没有上传文件'}), 400
            
            file = request.files['file']
            
            # 检查文件名是否为空
            if file.filename == '':
                return jsonify({'error': '未选择文件'}), 400
            
            # 检查文件类型
            if not allowed_file(file.filename):
                return jsonify({'error': '不支持的文件类型，仅支持: png, jpg, jpeg, gif, webp'}), 400
            
            # 检查文件大小（头像限制为5MB）
            file.seek(0, os.SEEK_END)
            file_size = file.tell()
            file.seek(0)
            
            MAX_AVATAR_SIZE = 5 * 1024 * 1024  # 5MB
            if file_size > MAX_AVATAR_SIZE:
                return jsonify({'error': f'文件大小超过限制（最大{MAX_AVATAR_SIZE//1024//1024}MB）'}), 400
            
            # 生成安全的文件名
            filename = secure_filename(file.filename)
            
            # 添加时间戳避免文件名冲突
            from datetime import datetime
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            name, ext = os.path.splitext(filename)
            filename = f"pet_avatar_{timestamp}{ext}"
            
            # 保存文件到头像文件夹
            filepath = os.path.join(AVATAR_FOLDER, filename)
            file.save(filepath)
            
            # 生成文件URL
            file_url = f'/static/avatars/{filename}'
            
            # 注意：这里不更新用户的avatar_url
            
            return jsonify({
                'success': True,
                'file_url': file_url,
                'filename': filename,
                'file_size': file_size
            }), 201
            
        except Exception as e:
            return jsonify({'error': '宠物头像上传失败', 'details': str(e)}), 500