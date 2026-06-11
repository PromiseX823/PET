from flask import jsonify, request
from werkzeug.security import check_password_hash, generate_password_hash
from models.users import users
from datetime import datetime


def register_auth_routes(app):
    """注册认证相关的路由"""
    
    @app.route('/api/login', methods=['POST'])
    def login():
        """用户登录接口"""
        data = request.json
        username = data.get('username')
        password = data.get('password')
        
        if not username or not password:
            return jsonify({'success': False, 'error': '用户名和密码不能为空'})
        
        # 查找用户
        user = users.select().where(users.username == username).first()
        if not user:
            return jsonify({'success': False, 'error': '用户不存在'})
        
        # 验证密码
        if not check_password_hash(user.password, password):
            return jsonify({'success': False, 'error': '密码错误'})
        
        # 返回用户信息（不包含密码）
        user_data = {
            'id': user.id,
            'username': user.username,
            'email': user.email,
            'role': user.role,
            'phone': user.phone,
            'address': user.address,
            'bio': user.bio,
            'avatar_url': f'http://localhost:5000{user.avatar_url}',
            'created_at': user.created_at.strftime('%Y-%m-%d %H:%M:%S') if user.created_at else None
        }
        
        return jsonify({'success': True, 'user': user_data})
    
    @app.route('/api/register', methods=['POST'])
    def register():
        """用户注册接口"""
        data = request.json
        username = data.get('username')
        email = data.get('email')
        password = data.get('password')
        
        # 验证必要字段
        if not username or not email or not password:
            return jsonify({'error': '用户名、邮箱和密码不能为空'}), 400
        
        # 验证用户名长度
        if len(username) < 3 or len(username) > 20:
            return jsonify({'error': '用户名长度必须在3-20个字符之间'}), 400
        
        # 验证密码长度
        if len(password) < 6:
            return jsonify({'error': '密码长度不能少于6个字符'}), 400
        
        # 检查用户名是否已存在
        if users.select().where(users.username == username).first():
            return jsonify({'error': '用户名已存在'}), 400
        
        # 检查邮箱是否已存在
        if users.select().where(users.email == email).first():
            return jsonify({'error': '邮箱已被注册'}), 400
        
        # 创建新用户
        try:
            user = users.create(
                username=username,
                email=email,
                password=generate_password_hash(password),
                role='user',
                phone=data.get('phone'),
                address=data.get('address'),
                bio=data.get('bio', ''),
                avatar_url='/static/avatars/user1_avatar.jpg',
                created_at=datetime.now(),
                updated_at=datetime.now()
            )
            
            # 返回创建的用户信息
            user_data = {
                'id': user.id,
                'username': user.username,
                'email': user.email,
                'role': user.role,
                'phone': user.phone,
                'address': user.address,
                'bio': user.bio,
                'avatar_url': f'http://localhost:5000{user.avatar_url}',
                'created_at': user.created_at.strftime('%Y-%m-%d %H:%M:%S') if user.created_at else None
            }
            
            return jsonify({'success': True, 'user': user_data}), 201
        except Exception as e:
            return jsonify({'error': '注册失败', 'details': str(e)}), 500





