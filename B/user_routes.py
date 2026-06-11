from flask import jsonify, request
from models.users import users
from models.pets import pets
from models.photos import photos
from models.adoptions import adoptions
from models.favorites import favorites
from models.likes import likes
from models.photo_collections import photo_collections
from datetime import datetime


def register_user_routes(app):
    """注册用户相关的路由"""
    
    @app.route('/api/users', methods=['GET'])
    def get_users():
        """获取用户列表，支持分页、筛选和排序"""
        # 获取查询参数
        page = int(request.args.get('page', 1))
        page_size = int(request.args.get('page_size', 10))
        username = request.args.get('username')
        email = request.args.get('email')
        role = request.args.get('role')
        sort_by = request.args.get('sort_by', 'username')
        sort_order = request.args.get('sort_order', 'desc')
        
        # 构建查询
        query = users.select()
        
        # 添加筛选条件
        if username:
            query = query.where(users.username.contains(username))
        if email:
            query = query.where(users.email.contains(email))
        if role:
            query = query.where(users.role == role)
        
        # 强制按用户名排序
        if sort_order == 'desc':
            query = query.order_by(users.username.desc())
        else:
            query = query.order_by(users.username.asc())
        
        # 计算总数
        total = query.count()
        
        # 分页
        offset = (page - 1) * page_size
        paginated_query = query.offset(offset).limit(page_size)
        
        # 构建返回数据
        all_users = []
        for user in paginated_query:
            user_data = {
                'id': user.id,
                'username': user.username,
                'email': user.email,
                'role': user.role,
                'phone': user.phone,
                'address': user.address,
                'bio': user.bio,
                'avatar_url': f'http://localhost:5000{user.avatar_url}',
                'created_at': user.created_at.strftime('%Y-%m-%d %H:%M:%S') if user.created_at else None,
                'updated_at': user.updated_at.strftime('%Y-%m-%d %H:%M:%S') if user.updated_at else None
            }
            all_users.append(user_data)
        
        return jsonify({
            'users': all_users,
            'total': total,
            'page': page,
            'page_size': page_size,
            'total_pages': (total + page_size - 1) // page_size
        })
    
    @app.route('/api/users/<int:user_id>', methods=['GET'])
    def get_user(user_id):
        """获取单个用户详情，包含关联数据"""
        user = users.select().where(users.id == user_id).first()
        if not user:
            return jsonify({'error': '用户不存在'}), 404
        
        # 获取用户关联的宠物数据
        user_pets = []
        for pet in pets.select().where(pets.owner_id == user_id):
            # 获取宠物的主照片
            main_photo = photos.select().where(
                (photos.pet_id == pet.id) & (photos.is_main == True)
            ).first()
            
            pet_data = {
                'id': pet.id,
                'name': pet.name,
                'avatar': f'http://localhost:5000{main_photo.image_url}' if main_photo else '',
                'breed': pet.breed,
                'age': pet.age,
                'gender': pet.gender,
                'status': pet.status,
                'description': pet.description,
                'health_info': pet.health_info,
                'location': pet.location,
                'weight': pet.weight,
                'color': pet.color,
                'neutered': pet.neutered,
                'vaccinated': pet.vaccinated,
                'created_at': pet.created_at.strftime('%Y-%m-%d %H:%M:%S') if pet.created_at else None
            }
            user_pets.append(pet_data)
        
        # 获取用户关联的照片数据
        user_photos = []
        for photo in photos.select().where(photos.user_id == user_id):
            photo_data = {
                'id': photo.id,
                'url': f'http://localhost:5000{photo.image_url}',
                'caption': photo.caption,
                'likes': 0,  # 示例数据
                'isLiked': False,  # 示例数据
                'createdAt': photo.upload_time.strftime('%Y-%m-%d %H:%M:%S') if photo.upload_time else None
            }
            user_photos.append(photo_data)
        
        # 获取用户关联的申请记录（简化版）
        user_applications = []
        for adoption in adoptions.select().where(adoptions.applicant_id == user_id):
            # 获取宠物信息
            pet = pets.select().where(pets.id == adoption.pet_id).first()
            if pet:
                main_photo = photos.select().where(
                    (photos.pet_id == pet.id) & (photos.is_main == True)
                ).first()
                
                application_data = {
                    'id': adoption.id,
                    'pet': {
                        'id': pet.id,
                        'name': pet.name,
                        'avatar': f'http://localhost:5000{main_photo.image_url}' if main_photo else ''
                    },
                    'description': adoption.applicant_note,
                    'status': adoption.status,
                    'createdAt': adoption.apply_time.strftime('%Y-%m-%d %H:%M:%S') if adoption.apply_time else None
                }
                user_applications.append(application_data)
        
        # 构造完整的用户数据，确保字段名称与前端一致
        user_data = {
            'id': user.id,
            'username': user.username,
            'nickname': user.username,  # 暂时使用username作为nickname
            'email': user.email,
            'role': user.role,
            'phone': user.phone,
            'address': user.address,
            'description': user.bio,  # 将bio映射为description
            'avatar': f'http://localhost:5000{user.avatar_url}',  # 将avatar_url映射为avatar
            'created_at': user.created_at.strftime('%Y-%m-%d %H:%M:%S') if user.created_at else None,
            'updated_at': user.updated_at.strftime('%Y-%m-%d %H:%M:%S') if user.updated_at else None,
            # 添加统计数据
            'followingCount': 0,  # 示例数据
            'followerCount': 0,  # 示例数据
            'petCount': len(user_pets),
            'photoCount': len(user_photos),
            # 添加关联数据
            'pets': user_pets,
            'photos': user_photos,
            'applications': user_applications,
            'comments': []  # 暂时为空
        }
        
        return jsonify(user_data)
    
    @app.route('/api/users', methods=['POST'])
    def add_user():
        """创建新用户（管理员功能）"""
        try:
            data = request.get_json()
            
            # 验证必要的字段
            if not data or not data.get('username') or not data.get('email') or not data.get('password'):
                return jsonify({'error': '缺少必要的字段'}), 400
            
            # 检查用户名是否已存在
            if users.select().where(users.username == data['username']).first():
                return jsonify({'error': '用户名已存在'}), 400
            
            # 检查邮箱是否已存在
            if users.select().where(users.email == data['email']).first():
                return jsonify({'error': '邮箱已被注册'}), 400
            
            # 创建新用户
            from werkzeug.security import generate_password_hash
            user = users.create(
                username=data.get('username'),
                email=data.get('email'),
                password=generate_password_hash(data.get('password')),
                role=data.get('role', 'user'),
                phone=data.get('phone'),
                address=data.get('address'),
                bio=data.get('bio', ''),
                avatar_url=data.get('avatar_url', '/static/avatars/default_avatar.jpg'),
                created_at=datetime.now(),
                updated_at=datetime.now()
            )
            
            # 返回创建的用户信息
            user_data = {
                'id': user.id,
                'username': user.username,
                'email': user.email,
                'role': user.role,
                'avatar_url': f'http://localhost:5000{user.avatar_url}'
            }
            
            return jsonify({'success': True, 'user': user_data}), 201
        except Exception as e:
            return jsonify({'error': '创建用户失败', 'details': str(e)}), 500
    
    @app.route('/api/users/<int:user_id>', methods=['PUT'])
    def update_user(user_id):
        """更新用户信息"""
        try:
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            data = request.get_json()
            
            # 更新用户信息
            update_fields = {}
            if 'username' in data:
                update_fields['username'] = data['username']
            # 处理前端发送的nickname字段
            if 'nickname' in data:
                update_fields['username'] = data['nickname']
            if 'email' in data:
                update_fields['email'] = data['email']
            if 'role' in data:
                update_fields['role'] = data['role']
            if 'phone' in data:
                update_fields['phone'] = data['phone']
            if 'address' in data:
                update_fields['address'] = data['address']
            # 处理前端发送的area字段
            if 'area' in data:
                update_fields['address'] = data['area']
            if 'bio' in data:
                update_fields['bio'] = data['bio']
            # 处理前端发送的description字段
            if 'description' in data:
                update_fields['bio'] = data['description']
            if 'avatar_url' in data:
                update_fields['avatar_url'] = data['avatar_url']
            # 处理前端发送的avatar字段
            if 'avatar' in data:
                # 移除完整URL前缀，只保留相对路径
                avatar_path = data['avatar']
                if avatar_path.startswith('http://localhost:5000'):
                    avatar_path = avatar_path.replace('http://localhost:5000', '')
                update_fields['avatar_url'] = avatar_path
            
            # 如果提供了新密码，则更新密码
            if 'password' in data:
                from werkzeug.security import generate_password_hash
                update_fields['password'] = generate_password_hash(data['password'])
            
            # 更新时间
            update_fields['updated_at'] = datetime.now()
            
            if update_fields:
                users.update(update_fields).where(users.id == user_id).execute()
            
            # 查询并返回更新后的用户信息
            updated_user = users.select().where(users.id == user_id).first()
            return jsonify({
                'id': updated_user.id,
                'username': updated_user.username,
                'email': updated_user.email,
                'phone': updated_user.phone,
                'address': updated_user.address,
                'bio': updated_user.bio,
                'avatar_url': f'http://localhost:5000{updated_user.avatar_url}'
            }), 200
        except Exception as e:
            return jsonify({'error': '更新用户失败', 'details': str(e)}), 500
    
    @app.route('/api/users/<int:user_id>', methods=['DELETE'])
    def delete_user(user_id):
        """删除用户"""
        try:
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 使用事务保证所有操作要么都成功，要么都失败
            from models.base import db
            with db.atomic():
                # 删除用户发布的照片
                photos.delete().where(photos.user_id == user_id).execute()
                
                # 删除用户拥有的宠物
                pets.delete().where(pets.owner_id == user_id).execute()
                
                # 删除用户的领养申请
                adoptions.delete().where(adoptions.applicant_id == user_id).execute()
                
                # 删除用户的收藏
                favorites.delete().where(favorites.user_id == user_id).execute()
                
                # 删除用户
                users.delete().where(users.id == user_id).execute()
            
            return jsonify({'message': '用户删除成功'}), 200
        except Exception as e:
            return jsonify({'error': '删除用户失败', 'details': str(e)}), 500
    
    @app.route('/api/users/<int:user_id>/pets', methods=['GET'])
    def get_user_pets(user_id):
        """获取用户拥有的宠物列表"""
        try:
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            user_pets = []
            for pet in pets.select().where(pets.owner_id == user_id):
                pet_data = {
                    'id': pet.id,
                    'name': pet.name,
                    'type': pet.type,
                    'age': pet.age,
                    'breed': pet.breed,
                    'gender': pet.gender,
                    'status': pet.status,
                    'description': pet.description,
                    'health_info': pet.health_info,
                    'location': pet.location,
                    'weight': pet.weight,
                    'color': pet.color,
                    'neutered': pet.neutered,
                    'vaccinated': pet.vaccinated,
                    'created_at': pet.created_at.strftime('%Y-%m-%d %H:%M:%S') if pet.created_at else None,
                    'updated_at': pet.updated_at.strftime('%Y-%m-%d %H:%M:%S') if pet.updated_at else None
                }
                
                # 获取宠物的主照片
                main_photo = photos.select().where(
                    (photos.pet_id == pet.id) & (photos.is_main == True)
                ).first()
                if main_photo:
                    pet_data['main_photo'] = {
                        'id': main_photo.id,
                        'image_url': f'http://localhost:5000{main_photo.image_url}',
                        'caption': main_photo.caption
                    }
                
                user_pets.append(pet_data)
            
            return jsonify(user_pets)
        except Exception as e:
            return jsonify({'error': '获取用户宠物失败', 'details': str(e)}), 500
    
    @app.route('/api/users/self/delete', methods=['POST'])
    def delete_self():
        """用户自己删除自己的账号"""
        try:
            data = request.get_json()
            
            # 验证必要的字段
            if not data or not data.get('user_id') or not data.get('password'):
                return jsonify({'error': '缺少必要的字段: user_id 和 password'}), 400
            
            user_id = data['user_id']
            password = data['password']
            
            # 验证用户是否存在
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 验证密码
            from werkzeug.security import check_password_hash
            if not check_password_hash(user.password, password):
                return jsonify({'error': '密码错误'}), 401
            
            # 使用事务保证所有操作要么都成功，要么都失败
            from models.base import db
            with db.atomic():
                # 删除用户发布的照片
                photos.delete().where(photos.user_id == user_id).execute()
                
                # 删除用户拥有的宠物
                pets.delete().where(pets.owner_id == user_id).execute()
                
                # 删除用户的领养申请
                adoptions.delete().where(adoptions.applicant_id == user_id).execute()
                
                # 删除用户的收藏
                favorites.delete().where(favorites.user_id == user_id).execute()
                
                # 删除用户
                users.delete().where(users.id == user_id).execute()
            
            return jsonify({'message': '账号删除成功'}), 200
        except Exception as e:
            return jsonify({'error': '删除账号失败', 'details': str(e)}), 500
    
    @app.route('/api/users/<int:user_id>/liked-photos', methods=['GET'])
    def get_user_liked_photos(user_id):
        """获取用户所有点赞的照片ID列表"""
        try:
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 获取用户所有点赞的照片ID
            liked_photos = likes.select(likes.photo_id).where(likes.user_id == user_id)
            liked_photo_ids = [like.photo_id for like in liked_photos]
            
            return jsonify({
                'user_id': user_id,
                'liked_photo_ids': liked_photo_ids,
                'count': len(liked_photo_ids)
            }), 200
        except Exception as e:
            return jsonify({'error': '获取用户点赞照片失败', 'details': str(e)}), 500
    
    @app.route('/api/users/<int:user_id>/collected-photos', methods=['GET'])
    def get_user_collected_photos(user_id):
        """获取用户所有收藏的照片ID列表"""
        try:
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 获取用户所有收藏的照片ID
            collected_photos = photo_collections.select(photo_collections.photo_id).where(photo_collections.user_id == user_id)
            collected_photo_ids = [collection.photo_id for collection in collected_photos]
            
            return jsonify({
                'user_id': user_id,
                'collected_photo_ids': collected_photo_ids,
                'count': len(collected_photo_ids)
            }), 200
        except Exception as e:
            return jsonify({'error': '获取用户收藏照片失败', 'details': str(e)}), 500
