from flask import jsonify, request
from models.users import users
from models.pets import pets
from models.photos import photos
from models.adoptions import adoptions
from models.notifications import notifications
from models.likes import likes
from models.comments import comments
from models.base import db
from peewee import fn
from datetime import datetime, date


def register_stats_routes(app):
    """注册统计相关的路由"""
    
    @app.route('/api/stats', methods=['GET'])
    def get_stats():
        """获取系统统计数据"""
        try:
            # 1. 基本统计数据
            stats = {
                # 用户统计
                'users': {
                    'total': users.select().count(),
                    'today': users.select().where(
                        fn.DATE(users.created_at) == date.today()
                    ).count(),
                    'pending': 0  # 没有用户待审核状态
                },
                
                # 宠物统计
                'pets': {
                    'total': pets.select().count(),
                    'available': pets.select().where(
                        (pets.status == '待领养') | (pets.status == 'available')
                    ).count(),
                    'adopted': pets.select().where(
                        (pets.status == '已领养') | (pets.status == 'adopted')
                    ).count(),
                    'pending': pets.select().where(
                        (pets.status == 'pending') | (pets.status == '待审核')
                    ).count()
                },
                
                # 领养申请统计
                'adoptions': {
                    'total': adoptions.select().count(),
                    'pending': adoptions.select().where(
                        (adoptions.status == 'pending') | (adoptions.status == '待审核')
                    ).count(),
                    'approved': adoptions.select().where(
                        (adoptions.status == 'approved') | (adoptions.status == '已批准')
                    ).count(),
                    'rejected': adoptions.select().where(
                        (adoptions.status == 'rejected') | (adoptions.status == '已拒绝')
                    ).count(),
                    'completed': adoptions.select().where(
                        (adoptions.status == 'completed') | (adoptions.status == '已完成')
                    ).count()
                },
                
                # 照片统计
                'photos': {
                    'total': photos.select().count(),
                    'today': photos.select().where(
                        fn.DATE(photos.upload_time) == date.today()
                    ).count()
                },
                
                # 互动统计
                'interactions': {
                    'likes': likes.select().count(),
                    'comments': comments.select().count()
                }
            }
            
            # 2. 按类型统计宠物数量
            pet_type_stats = []
            for row in pets.select(
                pets.type,
                fn.COUNT(pets.id).alias('count')
            ).group_by(pets.type):
                pet_type_stats.append({
                    'type': row.type,
                    'count': row.count
                })
            stats['pets']['by_type'] = pet_type_stats
            
            # 3. 按品种统计宠物数量（只统计数量大于0的品种）
            pet_breed_stats = []
            for row in pets.select(
                pets.breed,
                fn.COUNT(pets.id).alias('count')
            ).where(pets.breed != '').group_by(pets.breed).order_by(fn.COUNT(pets.id).desc()):
                pet_breed_stats.append({
                    'breed': row.breed,
                    'count': row.count
                })
            stats['pets']['by_breed'] = pet_breed_stats
            
            # 4. 按性别统计宠物数量
            pet_gender_stats = []
            for row in pets.select(
                pets.gender,
                fn.COUNT(pets.id).alias('count')
            ).group_by(pets.gender):
                pet_gender_stats.append({
                    'gender': row.gender,
                    'count': row.count
                })
            stats['pets']['by_gender'] = pet_gender_stats
            
            # 5. 按用户统计发布的宠物数量（只统计数量大于0的用户）
            user_pet_stats = []
            # 先获取所有有宠物的用户ID和数量
            user_pet_counts = pets.select(
                pets.owner_id,
                fn.COUNT(pets.id).alias('pet_count')
            ).group_by(pets.owner_id).having(fn.COUNT(pets.id) > 0).order_by(fn.COUNT(pets.id).desc())
            
            # 然后获取这些用户的详细信息
            for row in user_pet_counts:
                user = users.select().where(users.id == row.owner_id).first()
                if user:
                    user_pet_stats.append({
                        'user_id': user.id,
                        'username': user.username,
                        'pet_count': row.pet_count
                    })
            stats['users']['pet_contributions'] = user_pet_stats
            
            # 6. 统计每个宠物的照片数量
            pet_photo_stats = []
            # 先获取所有宠物的照片数量
            pet_photo_counts = photos.select(
                photos.pet_id,
                fn.COUNT(photos.id).alias('photo_count')
            ).group_by(photos.pet_id).order_by(fn.COUNT(photos.id).desc())
            
            # 然后获取这些宠物的详细信息
            for row in pet_photo_counts:
                pet = pets.select().where(pets.id == row.pet_id).first()
                if pet:
                    pet_photo_stats.append({
                        'pet_id': pet.id,
                        'pet_name': pet.name,
                        'photo_count': row.photo_count
                    })
            stats['pets']['photo_counts'] = pet_photo_stats
            
            # 7. 统计每个照片的点赞和评论数量
            photo_interaction_stats = []
            # 先获取所有照片的点赞数量
            photo_like_counts = likes.select(
                likes.photo_id,
                fn.COUNT(likes.id).alias('like_count')
            ).group_by(likes.photo_id).order_by(fn.COUNT(likes.id).desc())
            
            # 然后获取每个照片的评论数量并组合结果
            for like_row in photo_like_counts:
                comment_count = comments.select().where(comments.photo_id == like_row.photo_id).count()
                photo_interaction_stats.append({
                    'photo_id': like_row.photo_id,
                    'like_count': like_row.like_count,
                    'comment_count': comment_count
                })
            stats['photos']['interactions'] = photo_interaction_stats
            
            return jsonify({'success': True, 'data': stats}), 200
            
        except Exception as e:
            return jsonify({'error': '获取统计数据失败', 'details': str(e)}), 500
    
    @app.route('/api/stats/pets/type', methods=['GET'])
    def get_pet_type_stats():
        """按类型统计宠物数量"""
        try:
            stats = []
            for row in pets.select(
                pets.type,
                fn.COUNT(pets.id).alias('count')
            ).group_by(pets.type):
                stats.append({
                    'type': row.type,
                    'count': row.count
                })
            return jsonify({'success': True, 'data': stats}), 200
        except Exception as e:
            return jsonify({'error': '获取宠物类型统计失败', 'details': str(e)}), 500
    
    @app.route('/api/stats/pets/status', methods=['GET'])
    def get_pet_status_stats():
        """按状态统计宠物数量"""
        try:
            stats = []
            for row in pets.select(
                pets.status,
                fn.COUNT(pets.id).alias('count')
            ).group_by(pets.status):
                stats.append({
                    'status': row.status,
                    'count': row.count
                })
            return jsonify({'success': True, 'data': stats}), 200
        except Exception as e:
            return jsonify({'error': '获取宠物状态统计失败', 'details': str(e)}), 500
    
    @app.route('/api/stats/users/pets', methods=['GET'])
    def get_user_pet_stats():
        """按用户统计发布的宠物数量"""
        try:
            stats = []
            for row in users.select(
                users.id,
                users.username,
                fn.COUNT(pets.id).alias('pet_count')
            ).join(pets, on=(users.id == pets.owner_id), join_type='left').group_by(users.id, users.username).having(fn.COUNT(pets.id) > 0).order_by(fn.COUNT(pets.id).desc()):
                stats.append({
                    'user_id': row.id,
                    'username': row.username,
                    'pet_count': row.pet_count
                })
            return jsonify({'success': True, 'data': stats}), 200
        except Exception as e:
            return jsonify({'error': '获取用户宠物统计失败', 'details': str(e)}), 500
