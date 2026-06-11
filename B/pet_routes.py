from flask import jsonify, request
from models.pets import pets
from models.photos import photos
from models.users import users
from models.comments import comments
from models.favorites import favorites
from models.notifications import notifications
from datetime import datetime


def register_pet_routes(app):
    """注册宠物相关的路由"""
    
    @app.route('/api/pets', methods=['GET'])
    def get_pets():
        """获取所有宠物列表，支持分页、筛选和排序"""
        # 获取查询参数
        page = int(request.args.get('page', 1))
        page_size = int(request.args.get('page_size', 10))
        pet_type = request.args.get('type')
        status = request.args.get('status')
        breed = request.args.get('breed')
        gender = request.args.get('gender')
        location = request.args.get('location')
        sort_by = request.args.get('sort_by', 'created_at')
        sort_order = request.args.get('sort_order', 'desc')
        
        # 构建查询
        query = pets.select()
        
        # 添加筛选条件
        if pet_type:
            query = query.where(pets.type == pet_type)
        if status:
            query = query.where(pets.status == status)
        if breed:
            query = query.where(pets.breed.contains(breed))
        if gender:
            query = query.where(pets.gender == gender)
        if location:
            query = query.where(pets.location.contains(location))
        
        # 添加排序
        if sort_order == 'desc':
            query = query.order_by(getattr(pets, sort_by).desc())
        else:
            query = query.order_by(getattr(pets, sort_by).asc())
        
        # 计算总数
        total = query.count()
        
        # 分页
        if page_size > 0:
            offset = (page - 1) * page_size
            paginated_query = query.offset(offset).limit(page_size)
        else:
            # 当page_size为0时返回所有宠物
            paginated_query = query
        
        # 构建返回数据
        all_pets = []
        for pet in paginated_query:
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
                'owner_id': pet.owner_id,
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
                    'image_url': main_photo.image_url,
                    'caption': main_photo.caption
                }
            
            all_pets.append(pet_data)
        
        # 计算总页数
        if page_size > 0:
            total_pages = (total + page_size - 1) // page_size
        else:
            # 当page_size为0时，所有宠物在一页显示
            total_pages = 1
        
        return jsonify({
            'pets': all_pets,
            'total': total,
            'page': page,
            'page_size': page_size,
            'total_pages': total_pages
        })
    
    @app.route('/api/pets/<int:pet_id>', methods=['GET'])
    def get_pet(pet_id):
        """获取单个宠物详情"""
        pet = pets.select().where(pets.id == pet_id).first()
        if not pet:
            return jsonify({'error': '宠物不存在'}), 404
        
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
            'owner_id': pet.owner_id,
            'weight': pet.weight,
            'color': pet.color,
            'neutered': pet.neutered,
            'vaccinated': pet.vaccinated,
            'created_at': pet.created_at.strftime('%Y-%m-%d %H:%M:%S') if pet.created_at else None,
            'updated_at': pet.updated_at.strftime('%Y-%m-%d %H:%M:%S') if pet.updated_at else None
        }
        
        # 获取宠物的所有照片
        pet_photos = []
        for photo in photos.select().where(photos.pet_id == pet_id):
            pet_photos.append({
                'id': photo.id,
                'image_url': f'http://localhost:5000{photo.image_url}',
                'thumbnail_url': f'http://localhost:5000{photo.thumbnail_url}',
                'caption': photo.caption,
                'like_count': photo.like_count,
                'view_count': photo.view_count,
                'is_main': photo.is_main
            })
        pet_data['photos'] = pet_photos
        
        # 获取宠物的所有者信息
        owner = users.select().where(users.id == pet.owner_id).first()
        if owner:
            pet_data['owner'] = {
                'id': owner.id,
                'username': owner.username,
                'avatar_url': f'http://localhost:5000{owner.avatar_url}'
            }
        
        return jsonify(pet_data)
    
    @app.route('/api/pets', methods=['POST'])
    def add_pet():
        """创建新宠物"""
        try:
            data = request.get_json()
            
            # 验证必要的字段
            if not data or not data.get('name') or not data.get('type') or not data.get('owner_id'):
                return jsonify({'error': '缺少必要的字段'}), 400
            
            # 验证是否有照片
            if not data.get('photos') or len(data.get('photos')) == 0:
                return jsonify({'error': '宠物必须至少有一张照片'}), 400
            
            # 验证是否有主照片
            has_main_photo = any(photo.get('is_main') for photo in data.get('photos'))
            if not has_main_photo:
                return jsonify({'error': '必须指定一张主照片'}), 400
            
            # 使用事务保证宠物和照片要么都创建成功，要么都失败
            from models.base import db
            with db.atomic():
                # 创建宠物记录
                pet = pets.create(
                    name=data.get('name'),
                    type=data.get('type'),
                    age=data.get('age'),
                    breed=data.get('breed'),
                    gender=data.get('gender', 'unknown'),
                    status=data.get('status', '待领养'),
                    description=data.get('description'),
                    health_info=data.get('health_info'),
                    location=data.get('location'),
                    owner_id=data.get('owner_id'),
                    weight=data.get('weight'),
                    color=data.get('color'),
                    neutered=data.get('neutered', False),
                    vaccinated=data.get('vaccinated', False),
                    created_at=data.get('created_at') or datetime.now(),
                    updated_at=data.get('updated_at') or datetime.now()
                )
                
                # 创建照片记录
                for photo_data in data.get('photos'):
                    photos.create(
                        pet_id=pet.id,
                        user_id=data.get('owner_id'),
                        image_url=photo_data.get('image_url'),
                        thumbnail_url=photo_data.get('thumbnail_url', photo_data.get('image_url')),
                        caption=photo_data.get('caption', ''),
                        upload_time=datetime.now(),
                        is_main=photo_data.get('is_main', False)
                    )
            
            return jsonify({'message': '宠物信息创建成功', 'pet_id': pet.id}), 201
        except Exception as e:
            print(f'创建宠物失败: {e}')
            return jsonify({'error': '创建宠物失败', 'details': str(e)}), 500
    
    @app.route('/api/pets/<int:pet_id>', methods=['PUT'])
    def update_pet(pet_id):
        """更新宠物信息"""
        try:
            pet = pets.select().where(pets.id == pet_id).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            data = request.get_json()
            
            # 使用事务保证所有操作的一致性
            from models.base import db
            with db.atomic():
                # 更新宠物基本信息
                update_fields = {}
                if 'name' in data:
                    update_fields['name'] = data['name']
                if 'type' in data:
                    update_fields['type'] = data['type']
                if 'age' in data:
                    update_fields['age'] = data['age']
                if 'breed' in data:
                    update_fields['breed'] = data['breed']
                if 'gender' in data:
                    update_fields['gender'] = data['gender']
                if 'status' in data:
                    update_fields['status'] = data['status']
                if 'description' in data:
                    update_fields['description'] = data['description']
                if 'health_info' in data:
                    update_fields['health_info'] = data['health_info']
                if 'location' in data:
                    update_fields['location'] = data['location']
                if 'weight' in data:
                    update_fields['weight'] = data['weight']
                if 'color' in data:
                    update_fields['color'] = data['color']
                if 'neutered' in data:
                    update_fields['neutered'] = data['neutered']
                if 'vaccinated' in data:
                    update_fields['vaccinated'] = data['vaccinated']
                
                # 更新时间
                update_fields['updated_at'] = datetime.now()
                
                if update_fields:
                    pets.update(update_fields).where(pets.id == pet_id).execute()
                
                # 处理照片更新
                if 'photos' in data:
                    # 验证是否有主照片
                    has_main_photo = any(photo.get('is_main') for photo in data.get('photos'))
                    if not has_main_photo:
                        raise Exception('必须指定一张主照片')
                    
                    # 删除旧照片
                    photos.delete().where(photos.pet_id == pet_id).execute()
                    
                    # 创建新照片
                    for photo_data in data.get('photos'):
                        photos.create(
                            pet_id=pet_id,
                            user_id=pet.owner_id,
                            image_url=photo_data.get('image_url'),
                            thumbnail_url=photo_data.get('thumbnail_url', photo_data.get('image_url')),
                            caption=photo_data.get('caption', ''),
                            upload_time=datetime.now(),
                            is_main=photo_data.get('is_main', False)
                        )
            
            return jsonify({'message': '宠物信息更新成功'}), 200
        except Exception as e:
            print(f'更新宠物失败: {e}')
            return jsonify({'error': '更新宠物失败', 'details': str(e)}), 500
    
    @app.route('/api/pets/<int:pet_id>', methods=['DELETE'])
    def delete_pet(pet_id):
        """删除宠物"""
        try:
            pet = pets.select().where(pets.id == pet_id).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            # 使用事务保证删除宠物和照片的原子性
            from models.base import db
            with db.atomic():
                # 删除相关照片
                photos.delete().where(photos.pet_id == pet_id).execute()
                
                # 删除宠物
                pets.delete().where(pets.id == pet_id).execute()
            
            return jsonify({'message': '宠物删除成功'}), 200
        except Exception as e:
            print(f'删除宠物失败: {e}')
            return jsonify({'error': '删除宠物失败', 'details': str(e)}), 500
    
    @app.route('/api/pets/<int:pet_id>/comments', methods=['GET'])
    def get_pet_comments(pet_id):
        """获取宠物的所有评论，支持分页"""
        try:
            pet_comments = []
            
            # 验证宠物是否存在
            pet = pets.select().where(pets.id == pet_id).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            # 获取查询参数
            page = int(request.args.get('page', 1))
            page_size = int(request.args.get('page_size', 10))
            
            # 获取宠物的所有照片
            pet_photos = photos.select().where(photos.pet_id == pet_id)
            photo_ids = [photo.id for photo in pet_photos]
            
            # 构建查询
            query = comments.select().where(comments.photo_id.in_(photo_ids))
            
            # 按评论时间倒序排列
            query = query.order_by(comments.comment_time.desc())
            
            # 计算总数
            total = query.count()
            
            # 分页
            offset = (page - 1) * page_size
            paginated_query = query.offset(offset).limit(page_size)
            
            # 获取这些照片的所有评论
            for comment in paginated_query:
                # 处理日期字段
                if comment.comment_time:
                    if isinstance(comment.comment_time, str):
                        created_at_str = comment.comment_time
                    else:
                        created_at_str = comment.comment_time.strftime('%Y-%m-%d %H:%M:%S')
                else:
                    created_at_str = None
                
                comment_data = {
                    'id': comment.id,
                    'photo_id': comment.photo_id,
                    'user_id': comment.user_id,
                    'content': comment.content,
                    'parent_id': comment.parent_id,
                    'created_at': created_at_str
                }
                
                # 获取评论用户信息
                user = users.select().where(users.id == comment.user_id).first()
                if user:
                    comment_data['user'] = {
                        'id': user.id,
                        'username': user.username,
                        'avatar_url': f'http://localhost:5000{user.avatar_url}'
                    }
                
                # 如果是回复评论，获取被回复评论的用户信息
                if comment.parent_id:
                    parent_comment = comments.select().where(comments.id == comment.parent_id).first()
                    if parent_comment:
                        parent_user = users.select().where(users.id == parent_comment.user_id).first()
                        if parent_user:
                            comment_data['parent_user'] = {
                                'id': parent_user.id,
                                'username': parent_user.username
                            }
                
                pet_comments.append(comment_data)
            
            return jsonify({
                'comments': pet_comments,
                'total': total,
                'page': page,
                'page_size': page_size,
                'total_pages': (total + page_size - 1) // page_size
            })
        except Exception as e:
            print(f'获取宠物评论失败: {str(e)}')
            return jsonify({'error': '获取宠物评论失败', 'details': str(e)}), 500
    
    @app.route('/api/pets/<int:pet_id>/favorite', methods=['POST'])
    def favorite_pet(pet_id):
        """收藏宠物"""
        try:
            data = request.get_json()
            user_id = data.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少user_id'}), 400
            
            # 验证宠物是否存在
            pet = pets.select().where(pets.id == pet_id).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            # 验证用户是否存在
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 检查是否已经收藏
            existing_favorite = favorites.get_by_pet_user(pet_id, user_id)
            if existing_favorite:
                return jsonify({'error': '已经收藏过了'}), 400
            
            # 创建收藏记录
            favorites.create(
                pet_id=pet_id,
                user_id=user_id,
                like_time=datetime.now()
            )
            
            # 创建收藏通知
            if pet.owner_id != user_id:  # 不要给自己发通知
                pet_owner = users.select().where(users.id == pet.owner_id).first()
                if pet_owner:
                    notifications.create(
                        user_id=pet.owner_id,
                        title='有人收藏了你的宠物',
                        content=f'{user.username} 收藏了你的宠物 {pet.name}',
                        type='favorite',
                        is_read=False,
                        related_id=pet_id,
                        created_at=datetime.now()
                    )
                    print(f"创建收藏通知成功 - user_id: {pet.owner_id}, pet_id: {pet_id}")
            
            return jsonify({'message': '收藏成功'}), 200
        except Exception as e:
            print(f'收藏宠物失败: {str(e)}')
            return jsonify({'error': '收藏宠物失败', 'details': str(e)}), 500
    
    @app.route('/api/pets/<int:pet_id>/favorite', methods=['DELETE'])
    def unfavorite_pet(pet_id):
        """取消收藏宠物"""
        try:
            user_id = request.args.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少user_id'}), 400
            
            # 验证宠物是否存在
            pet = pets.select().where(pets.id == pet_id).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            # 删除收藏记录
            deleted_count = favorites.delete().where(
                (favorites.pet_id == pet_id) & (favorites.user_id == user_id)
            ).execute()
            
            if deleted_count == 0:
                return jsonify({'error': '未找到收藏记录'}), 404
            
            return jsonify({'message': '取消收藏成功'}), 200
        except Exception as e:
            print(f'取消收藏宠物失败: {str(e)}')
            return jsonify({'error': '取消收藏宠物失败', 'details': str(e)}), 500
