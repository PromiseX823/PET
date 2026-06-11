from flask import jsonify, request
from models.photos import photos
from models.pets import pets
from models.users import users
from models.comments import comments
from models.likes import likes
from models.photo_collections import photo_collections
from models.notifications import notifications
from datetime import datetime


def register_photo_routes(app):
    """注册照片相关的路由"""
    
    @app.route('/api/photos', methods=['GET'])
    def get_photos():
        """获取所有照片列表，支持排序"""
        # 获取查询参数
        sort_by = request.args.get('sort_by', 'upload_time')
        sort_order = request.args.get('sort_order', 'desc')
        limit = request.args.get('limit')
        
        # 构建查询
        query = photos.select()
        
        # 添加排序
        if sort_by and hasattr(photos, sort_by):
            if sort_order == 'desc':
                query = query.order_by(getattr(photos, sort_by).desc())
            else:
                query = query.order_by(getattr(photos, sort_by).asc())
        
        # 添加限制
        if limit:
            query = query.limit(int(limit))
        
        all_photos = []
        for photo in query:
            photo_data = {
                'id': photo.id,
                'pet_id': photo.pet_id,
                'user_id': photo.user_id,
                'image_url': f'http://localhost:5000{photo.image_url}',
                'thumbnail_url': f'http://localhost:5000{photo.thumbnail_url}',
                'caption': photo.caption,
                'like_count': photo.like_count,
                'view_count': photo.view_count,
                'comment_count': comments.select().where(comments.photo_id == photo.id).count(),
                'collection_count': photo_collections.select().where(photo_collections.photo_id == photo.id).count(),
                'is_main': photo.is_main,
                'upload_time': photo.upload_time if isinstance(photo.upload_time, str) else (
                    photo.upload_time.strftime('%Y-%m-%d %H:%M:%S') if photo.upload_time else None
                )
            }
            
            # 获取宠物信息
            pet = pets.select().where(pets.id == photo.pet_id).first()
            if pet:
                photo_data['pet'] = {
                    'id': pet.id,
                    'name': pet.name,
                    'type': pet.type
                }
            
            # 获取用户信息
            user = users.select().where(users.id == photo.user_id).first()
            if user:
                photo_data['user'] = {
                    'id': user.id,
                    'username': user.username,
                    'avatar_url': f'http://localhost:5000{user.avatar_url}'
                }
            
            all_photos.append(photo_data)
        
        return jsonify(all_photos)
    
    @app.route('/api/photos/hot', methods=['GET'])
    def get_hot_photos():
        """获取热门照片，按点赞数排序"""
        # 获取查询参数
        limit = request.args.get('limit', 3)
        
        # 构建查询：按点赞数倒序排序，限制数量
        query = photos.select().order_by(photos.like_count.desc()).limit(int(limit))
        
        hot_photos = []
        for photo in query:
            photo_data = {
                'id': photo.id,
                'pet_id': photo.pet_id,
                'user_id': photo.user_id,
                'image_url': f'http://localhost:5000{photo.image_url}',
                'thumbnail_url': f'http://localhost:5000{photo.thumbnail_url}',
                'caption': photo.caption,
                'like_count': photo.like_count,
                'view_count': photo.view_count,
                'comment_count': comments.select().where(comments.photo_id == photo.id).count(),
                'collection_count': photo_collections.select().where(photo_collections.photo_id == photo.id).count(),
                'is_main': photo.is_main,
                'upload_time': photo.upload_time if isinstance(photo.upload_time, str) else (
                    photo.upload_time.strftime('%Y-%m-%d %H:%M:%S') if photo.upload_time else None
                )
            }
            
            # 获取宠物信息
            pet = pets.select().where(pets.id == photo.pet_id).first()
            if pet:
                photo_data['pet'] = {
                    'id': pet.id,
                    'name': pet.name,
                    'type': pet.type
                }
            
            # 获取用户信息
            user = users.select().where(users.id == photo.user_id).first()
            if user:
                photo_data['user'] = {
                    'id': user.id,
                    'username': user.username,
                    'avatar_url': f'http://localhost:5000{user.avatar_url}'
                }
            
            hot_photos.append(photo_data)
        
        return jsonify(hot_photos)
    
    @app.route('/api/photos/<int:photo_id>', methods=['GET'])
    def get_photo(photo_id):
        """获取单个照片详情并增加浏览量"""
        photo = photos.select().where(photos.id == photo_id).first()
        if not photo:
            return jsonify({'error': '照片不存在'}), 404
        
        # 增加浏览量计数
        photos.update({
            'view_count': photo.view_count + 1
        }).where(photos.id == photo_id).execute()
        
        # 更新photo对象
        photo = photos.select().where(photos.id == photo_id).first()
        
        photo_data = {
            'id': photo.id,
            'pet_id': photo.pet_id,
            'user_id': photo.user_id,
            'image_url': f'http://localhost:5000{photo.image_url}',
            'thumbnail_url': f'http://localhost:5000{photo.thumbnail_url}',
            'caption': photo.caption,
            'like_count': photo.like_count,
            'view_count': photo.view_count,
            'comment_count': comments.select().where(comments.photo_id == photo.id).count(),
            'collection_count': photo_collections.select().where(photo_collections.photo_id == photo.id).count(),
            'is_main': photo.is_main,
            'upload_time': photo.upload_time if isinstance(photo.upload_time, str) else (
                photo.upload_time.strftime('%Y-%m-%d %H:%M:%S') if photo.upload_time else None
            )
        }
        
        # 获取宠物信息
        pet = pets.select().where(pets.id == photo.pet_id).first()
        if pet:
            photo_data['pet'] = {
                'id': pet.id,
                'name': pet.name,
                'type': pet.type,
                'breed': pet.breed,
                'age': pet.age,
                'gender': pet.gender,
                'status': pet.status
            }
        
        # 获取用户信息
        user = users.select().where(users.id == photo.user_id).first()
        if user:
            photo_data['user'] = {
                'id': user.id,
                'username': user.username,
                'avatar_url': f'http://localhost:5000{user.avatar_url}'
            }
        
        return jsonify(photo_data)
    
    @app.route('/api/photos', methods=['POST'])
    def add_photo():
        """上传新照片"""
        try:
            data = request.get_json()
            
            # 验证必要的字段
            if not data or not data.get('user_id') or not data.get('image_url'):
                return jsonify({'error': '缺少必要的字段'}), 400
            
            # 如果提供了pet_id，验证宠物是否存在且属于当前用户
            pet_id = data.get('pet_id')
            if pet_id:
                pet = pets.select().where(pets.id == pet_id).first()
                if not pet:
                    return jsonify({'error': '宠物不存在'}), 404
                # 验证宠物是否属于当前用户
                if pet.owner_id != data['user_id']:
                    return jsonify({'error': '无权为该宠物上传照片'}), 403
            
            # 验证用户是否存在
            user = users.select().where(users.id == data['user_id']).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 创建照片记录
            photo = photos.create(
                pet_id=data.get('pet_id'),
                user_id=data.get('user_id'),
                image_url=data.get('image_url'),
                thumbnail_url=data.get('thumbnail_url', data.get('image_url')),
                caption=data.get('caption', ''),
                like_count=0,
                view_count=0,
                is_main=data.get('is_main', False),
                upload_time=datetime.now()
            )
            
            # 返回创建的照片信息
            photo_data = {
                'id': photo.id,
                'pet_id': photo.pet_id,
                'user_id': photo.user_id,
                'image_url': f'http://localhost:5000{photo.image_url}',
                'thumbnail_url': f'http://localhost:5000{photo.thumbnail_url}',
                'caption': photo.caption,
                'like_count': photo.like_count,
                'view_count': photo.view_count,
                'is_main': photo.is_main,
                'upload_time': photo.upload_time.strftime('%Y-%m-%d %H:%M:%S')
            }
            
            return jsonify({'success': True, 'photo': photo_data}), 201
        except Exception as e:
            return jsonify({'error': '上传照片失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>', methods=['PUT'])
    def update_photo(photo_id):
        """更新照片信息"""
        try:
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                return jsonify({'error': '照片不存在'}), 404
            
            data = request.get_json()
            
            # 更新照片信息
            update_fields = {}
            if 'caption' in data:
                update_fields['caption'] = data['caption']
            if 'is_main' in data:
                update_fields['is_main'] = data['is_main']
            
            if update_fields:
                photos.update(update_fields).where(photos.id == photo_id).execute()
            
            return jsonify({'message': '照片信息更新成功'}), 200
        except Exception as e:
            return jsonify({'error': '更新照片失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>', methods=['DELETE'])
    def delete_photo(photo_id):
        """删除照片"""
        try:
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                return jsonify({'error': '照片不存在'}), 404
            
            # 删除相关评论
            comments.delete().where(comments.photo_id == photo_id).execute()
            
            # 删除相关点赞
            likes.delete().where(likes.photo_id == photo_id).execute()
            
            # 删除照片
            photos.delete().where(photos.id == photo_id).execute()
            
            return jsonify({'message': '照片删除成功'}), 200
        except Exception as e:
            return jsonify({'error': '删除照片失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/comments', methods=['GET'])
    def get_photo_comments(photo_id):
        """获取照片的评论"""
        try:
            photo_comments = []
            
            # 验证照片是否存在
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                return jsonify({'error': '照片不存在'}), 404
            
            # 查询该照片的所有评论
            for comment in comments.select().where(comments.photo_id == photo_id):
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
                
                photo_comments.append(comment_data)
            
            return jsonify(photo_comments)
        except Exception as e:
            return jsonify({'error': '获取照片评论失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/comments', methods=['POST'])
    def add_photo_comment(photo_id):
        """创建照片评论"""
        try:
            data = request.get_json()
            
            # 验证必要字段
            if not data or not data.get('user_id') or not data.get('content'):
                return jsonify({'error': '缺少必要的字段: user_id 和 content'}), 400
            
            # 验证照片是否存在
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                return jsonify({'error': '照片不存在'}), 404
            
            # 创建评论记录
            comment = comments.create(
                photo_id=photo_id,
                user_id=data['user_id'],
                content=data['content'],
                parent_id=data.get('parent_id'),
                comment_time=datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            )
            
            # 返回新创建的评论信息
            user = users.select().where(users.id == data['user_id']).first()
            comment_data = {
                'id': comment.id,
                'photo_id': comment.photo_id,
                'user_id': comment.user_id,
                'content': comment.content,
                'parent_id': comment.parent_id,
                'created_at': comment.comment_time
            }
            
            if user:
                comment_data['user'] = {
                    'id': user.id,
                    'username': user.username,
                    'avatar_url': f'http://localhost:5000{user.avatar_url}'
                }
            
            # 创建评论通知
            # 为照片所有者创建通知
            if photo.user_id != data['user_id']:  # 不要给自己发通知
                photo_owner = users.select().where(users.id == photo.user_id).first()
                if photo_owner:
                    notifications.create(
                        user_id=photo.user_id,
                        title='有人评论了你的照片',
                        content=f'{user.username} 评论了你的照片: {data.get("content")[:20]}...',
                        type='comment',
                        is_read=False,
                        related_id=photo_id,
                        created_at=datetime.now()
                    )
                    print(f"创建评论通知成功 - user_id: {photo.user_id}, photo_id: {photo_id}")
            
            # 如果是回复评论，为被回复的评论者创建通知
            parent_id = data.get('parent_id')
            if parent_id:
                parent_comment = comments.select().where(comments.id == parent_id).first()
                if parent_comment and parent_comment.user_id != data['user_id']:  # 不要给自己发通知
                    parent_user = users.select().where(users.id == parent_comment.user_id).first()
                    if parent_user:
                        notifications.create(
                            user_id=parent_comment.user_id,
                            title='有人回复了你的评论',
                            content=f'{user.username} 回复了你的评论: {data.get("content")[:20]}...',
                            type='comment',
                            is_read=False,
                            related_id=parent_id,
                            created_at=datetime.now()
                        )
                        print(f"创建回复通知成功 - user_id: {parent_comment.user_id}, comment_id: {parent_id}")
            
            return jsonify(comment_data), 201
        except Exception as e:
            return jsonify({'error': '添加评论失败', 'details': str(e)}), 500
    
    @app.route('/api/comments/<int:comment_id>', methods=['DELETE'])
    def delete_comment(comment_id):
        """删除评论"""
        try:
            # 验证评论是否存在
            comment = comments.select().where(comments.id == comment_id).first()
            if not comment:
                return jsonify({'error': '评论不存在'}), 404
            
            # 删除评论
            comments.delete().where(comments.id == comment_id).execute()
            
            # 删除该评论的所有回复
            comments.delete().where(comments.parent_id == comment_id).execute()
            
            return jsonify({'message': '评论删除成功'}), 200
        except Exception as e:
            print(f'删除评论失败: {e}')
            return jsonify({'error': '删除评论失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/like', methods=['POST'])
    def like_photo(photo_id):
        """点赞照片"""
        try:
            print(f"点赞请求 - photo_id: {photo_id}")
            print(f"请求方法: {request.method}")
            print(f"请求头: {request.headers}")
            print(f"请求体原始数据: {request.data}")
            
            # 尝试解析请求体
            try:
                data = request.get_json()
                print(f"解析后的请求体: {data}")
            except Exception as e:
                print(f"请求体解析失败: {str(e)}")
                print(f"Content-Type: {request.headers.get('Content-Type')}")
                return jsonify({'error': '请求体格式错误'}), 400
            
            user_id = data.get('user_id')
            print(f"获取的user_id: {user_id}")
            
            if not user_id:
                print("缺少user_id，返回400")
                return jsonify({'error': '缺少user_id'}), 400
            
            # 验证照片是否存在
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                print(f"照片 {photo_id} 不存在，返回404")
                return jsonify({'error': '照片不存在'}), 404
            
            # 检查是否已经点赞
            existing_like = likes.select().where(
                (likes.photo_id == photo_id) & (likes.user_id == user_id)
            ).first()
            
            if existing_like:
                print(f"用户 {user_id} 已经点赞过照片 {photo_id}，返回400")
                return jsonify({'error': '已经点赞过了'}), 400
            
            # 创建点赞记录
            likes.create(photo_id=photo_id, user_id=user_id)
            print(f"创建点赞记录成功 - photo_id: {photo_id}, user_id: {user_id}")
            
            # 更新照片的点赞数
            new_like_count = likes.select().where(likes.photo_id == photo_id).count()
            photos.update({'like_count': new_like_count}).where(photos.id == photo_id).execute()
            print(f"更新点赞数成功 - photo_id: {photo_id}, new_like_count: {new_like_count}")
            
            # 创建点赞通知
            if photo.user_id != user_id:  # 不要给自己发通知
                liker = users.select().where(users.id == user_id).first()
                if liker:
                    notifications.create(
                        user_id=photo.user_id,
                        title='有人点赞了你的照片',
                        content=f'{liker.username} 点赞了你的照片',
                        type='like',
                        is_read=False,
                        related_id=photo_id,
                        created_at=datetime.now()
                    )
                    print(f"创建点赞通知成功 - user_id: {photo.user_id}, photo_id: {photo_id}")
            
            return jsonify({'message': '点赞成功', 'like_count': new_like_count}), 200
        except Exception as e:
            print(f"点赞请求异常: {str(e)}")
            import traceback
            traceback.print_exc()
            return jsonify({'error': '点赞失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/unlike', methods=['POST'])
    def unlike_photo(photo_id):
        """取消点赞照片"""
        try:
            print(f"取消点赞请求 - photo_id: {photo_id}")
            print(f"请求方法: {request.method}")
            print(f"请求头: {request.headers}")
            print(f"请求体原始数据: {request.data}")
            
            # 尝试解析请求体
            try:
                data = request.get_json()
                print(f"解析后的请求体: {data}")
            except Exception as e:
                print(f"请求体解析失败: {str(e)}")
                print(f"Content-Type: {request.headers.get('Content-Type')}")
                return jsonify({'error': '请求体格式错误'}), 400
            
            user_id = data.get('user_id')
            print(f"获取的user_id: {user_id}")
            
            if not user_id:
                print("缺少user_id，返回400")
                return jsonify({'error': '缺少user_id'}), 400
            
            # 验证照片是否存在
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                print(f"照片 {photo_id} 不存在，返回404")
                return jsonify({'error': '照片不存在'}), 404
            
            # 删除点赞记录
            deleted_count = likes.delete().where(
                (likes.photo_id == photo_id) & (likes.user_id == user_id)
            ).execute()
            print(f"删除点赞记录 - 影响行数: {deleted_count}")
            
            # 更新照片的点赞数
            new_like_count = likes.select().where(likes.photo_id == photo_id).count()
            photos.update({'like_count': new_like_count}).where(photos.id == photo_id).execute()
            print(f"更新点赞数成功 - photo_id: {photo_id}, new_like_count: {new_like_count}")
            
            return jsonify({'message': '取消点赞成功', 'like_count': new_like_count}), 200
        except Exception as e:
            print(f"取消点赞请求异常: {str(e)}")
            import traceback
            traceback.print_exc()
            return jsonify({'error': '取消点赞失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/like-status', methods=['GET'])
    def get_photo_like_status(photo_id):
        """获取照片的点赞状态"""
        try:
            user_id = request.args.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少user_id'}), 400
            
            # 检查用户是否点赞了该照片
            is_liked = likes.select().where(
                (likes.photo_id == photo_id) & (likes.user_id == user_id)
            ).exists()
            
            # 获取照片的点赞数
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                return jsonify({'error': '照片不存在'}), 404
            
            return jsonify({
                'is_liked': is_liked,
                'like_count': photo.like_count
            }), 200
        except Exception as e:
            return jsonify({'error': '获取点赞状态失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/collect', methods=['POST'])
    def collect_photo(photo_id):
        """收藏照片"""
        try:
            data = request.get_json()
            user_id = data.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少user_id'}), 400
            
            # 验证照片是否存在
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                return jsonify({'error': '照片不存在'}), 404
            
            # 检查是否已经收藏
            existing_collection = photo_collections.select().where(
                (photo_collections.photo_id == photo_id) & (photo_collections.user_id == user_id)
            ).first()
            
            if existing_collection:
                return jsonify({'error': '已经收藏过了'}), 400
            
            # 创建收藏记录
            photo_collections.create(
                photo_id=photo_id,
                user_id=user_id,
                collection_time=datetime.now()
            )
            
            # 获取照片的收藏数
            collection_count = photo_collections.select().where(photo_collections.photo_id == photo_id).count()
            
            # 创建收藏通知
            if photo.user_id != user_id:  # 不要给自己发通知
                collector = users.select().where(users.id == user_id).first()
                if collector:
                    notifications.create(
                        user_id=photo.user_id,
                        title='有人收藏了你的照片',
                        content=f'{collector.username} 收藏了你的照片',
                        type='collection',
                        is_read=False,
                        related_id=photo_id,
                        created_at=datetime.now()
                    )
            
            return jsonify({'message': '收藏成功', 'collection_count': collection_count}), 200
        except Exception as e:
            return jsonify({'error': '收藏失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/uncollect', methods=['POST'])
    def uncollect_photo(photo_id):
        """取消收藏照片"""
        try:
            data = request.get_json()
            user_id = data.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少user_id'}), 400
            
            # 验证照片是否存在
            photo = photos.select().where(photos.id == photo_id).first()
            if not photo:
                return jsonify({'error': '照片不存在'}), 404
            
            # 删除收藏记录
            deleted_count = photo_collections.delete().where(
                (photo_collections.photo_id == photo_id) & (photo_collections.user_id == user_id)
            ).execute()
            
            # 获取照片的收藏数
            collection_count = photo_collections.select().where(photo_collections.photo_id == photo_id).count()
            
            return jsonify({'message': '取消收藏成功', 'collection_count': collection_count}), 200
        except Exception as e:
            return jsonify({'error': '取消收藏失败', 'details': str(e)}), 500
    
    @app.route('/api/photos/<int:photo_id>/collection-status', methods=['GET'])
    def get_photo_collection_status(photo_id):
        """获取照片的收藏状态"""
        try:
            user_id = request.args.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少user_id'}), 400
            
            # 检查用户是否收藏了该照片
            is_collected = photo_collections.select().where(
                (photo_collections.photo_id == photo_id) & (photo_collections.user_id == user_id)
            ).exists()
            
            # 获取照片的收藏数
            collection_count = photo_collections.select().where(photo_collections.photo_id == photo_id).count()
            
            return jsonify({
                'is_collected': is_collected,
                'collection_count': collection_count
            }), 200
        except Exception as e:
            return jsonify({'error': '获取收藏状态失败', 'details': str(e)}), 500

