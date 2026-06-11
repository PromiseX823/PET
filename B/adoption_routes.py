from flask import jsonify, request
from models.adoptions import adoptions
from models.pets import pets
from models.users import users
from models.notifications import notifications
from models.photos import photos
from datetime import datetime


def register_adoption_routes(app):
    """注册领养相关的路由"""
    
    @app.route('/api/adoptions', methods=['GET'])
    def get_adoptions():
        """获取所有领养申请列表，支持分页、筛选和排序"""
        all_adoptions = []
        
        # 获取查询参数
        status = request.args.get('status')
        user_id = request.args.get('user_id')
        pet_id = request.args.get('pet_id')
        page = int(request.args.get('page', 1))
        page_size = int(request.args.get('page_size', 10))
        
        # 构建查询条件
        query = adoptions.select()
        
        if status:
            query = query.where(adoptions.status == status)
        if user_id:
            query = query.where(adoptions.applicant_id == user_id)
        if pet_id:
            query = query.where(adoptions.pet_id == pet_id)
        
        # 按申请时间倒序排列
        query = query.order_by(adoptions.apply_time.desc())
        
        # 计算总数
        total = query.count()
        
        # 分页
        offset = (page - 1) * page_size
        paginated_query = query.offset(offset).limit(page_size)
        
        for adoption in paginated_query:
            adoption_data = {
                'id': adoption.id,
                'pet_id': adoption.pet_id,
                'applicant_id': adoption.applicant_id,
                'status': adoption.status,
                'apply_time': adoption.apply_time if isinstance(adoption.apply_time, str) else (
                    adoption.apply_time.strftime('%Y-%m-%d %H:%M:%S') if adoption.apply_time else None
                ),
                'review_time': adoption.review_time if isinstance(adoption.review_time, str) else (
                    adoption.review_time.strftime('%Y-%m-%d %H:%M:%S') if adoption.review_time else None
                ),
                'applicant_note': adoption.applicant_note,
                'admin_note': adoption.admin_note
            }
            
            # 获取宠物信息
            pet = pets.select().where(pets.id == adoption.pet_id).first()
            if pet:
                pet_data = {
                    'id': pet.id,
                    'name': pet.name,
                    'type': pet.type,
                    'breed': pet.breed,
                    'age': pet.age,
                    'gender': pet.gender,
                    'status': pet.status
                }
                # 获取宠物的主照片
                main_photo = photos.select().where(
                    (photos.pet_id == pet.id) & (photos.is_main == True)
                ).first()
                if main_photo:
                    pet_data['image_url'] = f'http://localhost:5000{main_photo.image_url}'
                adoption_data['pet'] = pet_data
            
            # 获取用户信息
            user = users.select().where(users.id == adoption.applicant_id).first()
            if user:
                adoption_data['user'] = {
                    'id': user.id,
                    'username': user.username,
                    'email': user.email,
                    'phone': user.phone,
                    'avatar_url': f'http://localhost:5000{user.avatar_url}'
                }

            all_adoptions.append(adoption_data)
        
        return jsonify({
            'adoptions': all_adoptions,
            'total': total,
            'page': page,
            'page_size': page_size,
            'total_pages': (total + page_size - 1) // page_size
        })
    
    @app.route('/api/adoptions/<int:adoption_id>', methods=['GET'])
    def get_adoption(adoption_id):
        """获取单个领养申请详情"""
        adoption = adoptions.select().where(adoptions.id == adoption_id).first()
        if not adoption:
            return jsonify({'error': '领养申请不存在'}), 404
        
        adoption_data = {
            'id': adoption.id,
            'pet_id': adoption.pet_id,
            'applicant_id': adoption.applicant_id,
            'status': adoption.status,
            'apply_time': adoption.apply_time if isinstance(adoption.apply_time, str) else (
                adoption.apply_time.strftime('%Y-%m-%d %H:%M:%S') if adoption.apply_time else None
            ),
            'review_time': adoption.review_time if isinstance(adoption.review_time, str) else (
                adoption.review_time.strftime('%Y-%m-%d %H:%M:%S') if adoption.review_time else None
            ),
            'applicant_note': adoption.applicant_note,
            'admin_note': adoption.admin_note
        }
        
        # 获取宠物信息
        pet = pets.select().where(pets.id == adoption.pet_id).first()
        if pet:
            pet_data = {
                'id': pet.id,
                'name': pet.name,
                'type': pet.type,
                'breed': pet.breed,
                'age': pet.age,
                'gender': pet.gender,
                'status': pet.status,
                'description': pet.description
            }
            # 获取宠物的主照片
            main_photo = photos.select().where(
                (photos.pet_id == pet.id) & (photos.is_main == True)
            ).first()
            if main_photo:
                pet_data['image_url'] = f'http://localhost:5000{main_photo.image_url}'
            adoption_data['pet'] = pet_data
        
        # 获取用户信息
        user = users.select().where(users.id == adoption.applicant_id).first()
        if user:
            adoption_data['user'] = {
                'id': user.id,
                'username': user.username,
                'email': user.email,
                'phone': user.phone,
                'address': user.address,
                'avatar_url': f'http://localhost:5000{user.avatar_url}'
            }

        return jsonify(adoption_data)
    
    @app.route('/api/adoptions', methods=['POST'])
    def create_adoption():
        """创建领养申请"""
        try:
            data = request.get_json()
            
            # 添加调试日志
            print(f"接收到的领养申请数据: {data}")
            
            # 验证必要的字段
            if not data or not data.get('pet_id') or not data.get('user_id'):
                return jsonify({'error': '缺少必要的字段: pet_id 和 user_id'}), 400
            
            # 验证宠物是否存在
            pet = pets.select().where(pets.id == data['pet_id']).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            # 验证宠物状态
            if pet.status != '待领养' and pet.status != 'available':
                return jsonify({'error': '该宠物暂不可领养'}), 400
            
            # 验证用户是否存在
            user = users.select().where(users.id == data['user_id']).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 检查用户是否已经申请过该宠物
            existing_adoption = adoptions.select().where(
                (adoptions.pet_id == data['pet_id']) & 
                (adoptions.applicant_id == data['user_id']) &
                (adoptions.status != 'rejected') &
                (adoptions.status != '已拒绝')
            ).first()
            
            if existing_adoption:
                return jsonify({'error': '您已经申请过该宠物'}), 400
            
            # 添加更多调试日志
            print(f"申请理由数据: {data.get('reason', '')}")
            print(f"申请理由类型: {type(data.get('reason', ''))}")
            
            # 创建领养申请记录
            adoption = adoptions.create(
                pet_id=data['pet_id'],
                applicant_id=data['user_id'],
                status='pending',
                apply_time=datetime.now(),
                applicant_note=data.get('reason', ''),
                admin_note=''
            )
            
            # 验证保存到数据库的数据
            saved_adoption = adoptions.select().where(adoptions.id == adoption.id).first()
            print(f"保存到数据库的申请理由: {saved_adoption.applicant_note}")
            
            # 创建通知给管理员
            notifications.create(
                user_id=1,  # 假设管理员ID为1
                type='adoption_request',
                content=f'用户 {user.username} 申请领养宠物 {pet.name}',
                related_id=adoption.id,
                is_read=False,
                created_at=datetime.now()
            )
            
            # 返回创建的领养申请信息
            adoption_data = {
                'id': adoption.id,
                'pet_id': adoption.pet_id,
                'applicant_id': adoption.applicant_id,
                'status': adoption.status,
                'apply_time': adoption.apply_time.strftime('%Y-%m-%d %H:%M:%S'),
                'applicant_note': adoption.applicant_note
            }

            return jsonify({'success': True, 'adoption': adoption_data}), 201
        except Exception as e:
            return jsonify({'error': '创建领养申请失败', 'details': str(e)}), 500
    
    @app.route('/api/adoptions/<int:adoption_id>/approve', methods=['POST'])
    def approve_adoption(adoption_id):
        """批准领养申请"""
        try:
            data = request.get_json()
            reviewer_id = data.get('reviewer_id')
            review_comment = data.get('review_comment', '')
            
            if not reviewer_id:
                return jsonify({'error': '缺少reviewer_id'}), 400
            
            # 验证领养申请是否存在
            adoption = adoptions.select().where(adoptions.id == adoption_id).first()
            if not adoption:
                return jsonify({'error': '领养申请不存在'}), 404
            
            # 验证申请状态
            if adoption.status != 'pending' and adoption.status != '待审核':
                return jsonify({'error': '该申请已被处理'}), 400
            
            # 获取宠物信息
            pet = pets.select().where(pets.id == adoption.pet_id).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            # 使用事务保证所有操作的原子性
            from models.base import db
            with db.atomic():
                # 更新领养申请状态
                adoptions.update({
                    'status': 'approved',
                    'review_time': datetime.now(),
                    'admin_note': review_comment
                }).where(adoptions.id == adoption_id).execute()
                
                # 更新宠物状态
                pets.update({'status': 'adopted'}).where(pets.id == adoption.pet_id).execute()
                
                # 拒绝该宠物的其他待处理申请
                other_adoptions = adoptions.select().where(
                    (adoptions.pet_id == adoption.pet_id) & 
                    (adoptions.id != adoption_id) &
                    ((adoptions.status == 'pending') | (adoptions.status == '待审核'))
                )
                
                for other_adoption in other_adoptions:
                    adoptions.update({
                        'status': 'rejected',
                        'review_time': datetime.now(),
                        'admin_note': '该宠物已被其他用户领养'
                    }).where(adoptions.id == other_adoption.id).execute()
                    
                    # 创建通知给被拒绝的用户
                    notifications.create(
                        user_id=other_adoption.applicant_id,
                        type='adoption_rejected',
                        content=f'您申请领养的宠物 {pet.name} 已被其他用户领养',
                        related_id=other_adoption.id,
                        is_read=False,
                        created_at=datetime.now()
                    )
                
                # 创建通知给申请人
                user = users.select().where(users.id == adoption.applicant_id).first()
                if user:
                    notifications.create(
                        user_id=user.id,
                        type='adoption_approved',
                        content=f'您申请领养的宠物 {pet.name} 已被批准',
                        related_id=adoption.id,
                        is_read=False,
                        created_at=datetime.now()
                    )
            
            return jsonify({'message': '领养申请已批准'}), 200
        except Exception as e:
            return jsonify({'error': '批准领养申请失败', 'details': str(e)}), 500
    
    @app.route('/api/adoptions/<int:adoption_id>/reject', methods=['POST'])
    def reject_adoption(adoption_id):
        """拒绝领养申请"""
        try:
            data = request.get_json()
            reviewer_id = data.get('reviewer_id')
            review_comment = data.get('review_comment', '')
            
            if not reviewer_id:
                return jsonify({'error': '缺少reviewer_id'}), 400
            
            # 验证领养申请是否存在
            adoption = adoptions.select().where(adoptions.id == adoption_id).first()
            if not adoption:
                return jsonify({'error': '领养申请不存在'}), 404
            
            # 验证申请状态
            if adoption.status != 'pending' and adoption.status != '待审核':
                return jsonify({'error': '该申请已被处理'}), 400
            
            # 更新领养申请状态
            adoptions.update({
                'status': 'rejected',
                'review_time': datetime.now(),
                'admin_note': review_comment
            }).where(adoptions.id == adoption_id).execute()
            
            # 获取宠物和用户信息
            pet = pets.select().where(pets.id == adoption.pet_id).first()
            user = users.select().where(users.id == adoption.applicant_id).first()
            
            # 创建通知给申请人
            if user and pet:
                notifications.create(
                    user_id=user.id,
                    type='adoption_rejected',
                    content=f'您申请领养的宠物 {pet.name} 未被批准',
                    related_id=adoption.id,
                    is_read=False,
                    created_at=datetime.now()
                )
            
            return jsonify({'message': '领养申请已拒绝'}), 200
        except Exception as e:
            return jsonify({'error': '拒绝领养申请失败', 'details': str(e)}), 500
    
    @app.route('/api/adoptions/<int:adoption_id>/cancel', methods=['POST'])
    def cancel_adoption(adoption_id):
        """取消领养申请"""
        try:
            # 验证领养申请是否存在
            adoption = adoptions.select().where(adoptions.id == adoption_id).first()
            if not adoption:
                return jsonify({'error': '领养申请不存在'}), 404
            
            # 验证申请状态
            if adoption.status != 'pending' and adoption.status != '待审核':
                return jsonify({'error': '只能取消待处理的申请'}), 400
            
            # 更新领养申请状态
            adoptions.update({
                'status': 'cancelled',
                'review_time': datetime.now()
            }).where(adoptions.id == adoption_id).execute()
            
            return jsonify({'message': '领养申请已取消'}), 200
        except Exception as e:
            return jsonify({'error': '取消领养申请失败', 'details': str(e)}), 500
    
    @app.route('/api/adoptions/<int:adoption_id>/complete', methods=['POST'])
    def complete_adoption(adoption_id):
        """完成领养"""
        try:
            # 验证领养申请是否存在
            adoption = adoptions.select().where(adoptions.id == adoption_id).first()
            if not adoption:
                return jsonify({'error': '领养申请不存在'}), 404
            
            # 验证申请状态
            if adoption.status != 'approved':
                return jsonify({'error': '只能完成已批准的申请'}), 400
            
            # 更新领养申请状态
            adoptions.update({
                'status': 'completed',
                'adoption_time': datetime.now()
            }).where(adoptions.id == adoption_id).execute()
            
            # 创建通知
            user = users.select().where(users.id == adoption.applicant_id).first()
            pet = pets.select().where(pets.id == adoption.pet_id).first()
            
            if user and pet:
                notifications.create(
                    user_id=user.id,
                    type='adoption_completed',
                    content=f'恭喜！您已成功领养宠物 {pet.name}',
                    related_id=adoption.id,
                    is_read=False,
                    created_at=datetime.now()
                )
            
            return jsonify({'message': '领养已完成'}), 200
        except Exception as e:
            return jsonify({'error': '完成领养失败', 'details': str(e)}), 500
    
    @app.route('/api/pets/<int:pet_id>/adoptions', methods=['GET'])
    def get_pet_adoptions(pet_id):
        """获取特定宠物的所有领养申请"""
        try:
            # 验证宠物是否存在
            pet = pets.select().where(pets.id == pet_id).first()
            if not pet:
                return jsonify({'error': '宠物不存在'}), 404
            
            # 查询该宠物的所有领养申请
            pet_adoptions = []
            for adoption in adoptions.select().where(adoptions.pet_id == pet_id).order_by(adoptions.apply_time.desc()):
                adoption_data = {
                    'id': adoption.id,
                    'pet_id': adoption.pet_id,
                    'user_id': adoption.applicant_id,
                    'status': adoption.status,
                    'apply_time': adoption.apply_time if isinstance(adoption.apply_time, str) else (
                        adoption.apply_time.strftime('%Y-%m-%d %H:%M:%S') if adoption.apply_time else None
                    ),
                    'applicant_note': adoption.applicant_note,
                    'admin_note': adoption.admin_note
                }
                
                # 获取用户信息
                user = users.select().where(users.id == adoption.applicant_id).first()
                if user:
                    adoption_data['user'] = {
                        'id': user.id,
                        'username': user.username,
                        'email': user.email,
                        'phone': user.phone,
                        'avatar_url': f'http://localhost:5000{user.avatar_url}'
                    }
                
                pet_adoptions.append(adoption_data)
            
            return jsonify(pet_adoptions)
        except Exception as e:
            return jsonify({'error': '获取宠物领养申请失败', 'details': str(e)}), 500
    
    @app.route('/api/adoptions/<int:adoption_id>', methods=['PUT'])
    def update_adoption(adoption_id):
        """更新领养申请信息"""
        try:
            # 获取请求数据
            data = request.get_json()
            
            # 验证领养申请是否存在
            adoption = adoptions.select().where(adoptions.id == adoption_id).first()
            if not adoption:
                return jsonify({'error': '领养申请不存在'}), 404
            
            # 验证用户是否存在
            user = users.select().where(users.id == adoption.applicant_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 使用事务保证所有操作的原子性
            from models.base import db
            with db.atomic():
                # 更新领养申请描述
                if 'description' in data:
                    adoptions.update({
                        'applicant_note': data['description']
                    }).where(adoptions.id == adoption_id).execute()
                
                # 更新用户联系方式
                user_update_fields = {}
                if 'phone' in data:
                    user_update_fields['phone'] = data['phone']
                if 'address' in data:
                    user_update_fields['address'] = data['address']
                
                if user_update_fields:
                    users.update(user_update_fields).where(users.id == adoption.applicant_id).execute()
            
            # 获取更新后的领养申请信息
            updated_adoption = adoptions.select().where(adoptions.id == adoption_id).first()
            updated_user = users.select().where(users.id == adoption.applicant_id).first()
            
            # 构造返回数据
            result = {
                'id': updated_adoption.id,
                'pet_id': updated_adoption.pet_id,
                'applicant_id': updated_adoption.applicant_id,
                'status': updated_adoption.status,
                'apply_time': updated_adoption.apply_time if isinstance(updated_adoption.apply_time, str) else (
                    updated_adoption.apply_time.strftime('%Y-%m-%d %H:%M:%S') if updated_adoption.apply_time else None
                ),
                'applicant_note': updated_adoption.applicant_note,
                'user': {
                    'id': updated_user.id,
                    'phone': updated_user.phone,
                    'address': updated_user.address
                }
            }
            
            return jsonify(result), 200
        except Exception as e:
            return jsonify({'error': '更新领养申请失败', 'details': str(e)}), 500

    @app.route('/api/users/<int:user_id>/adoptions', methods=['GET'])
    def get_user_adoptions(user_id):
        """获取特定用户的所有领养申请"""
        try:
            # 验证用户是否存在
            user = users.select().where(users.id == user_id).first()
            if not user:
                return jsonify({'error': '用户不存在'}), 404
            
            # 查询该用户的所有领养申请
            user_adoptions = []
            for adoption in adoptions.select().where(adoptions.applicant_id == user_id).order_by(adoptions.apply_time.desc()):
                adoption_data = {
                    'id': adoption.id,
                    'pet_id': adoption.pet_id,
                    'user_id': adoption.applicant_id,
                    'status': adoption.status,
                    'apply_time': adoption.apply_time if isinstance(adoption.apply_time, str) else (
                        adoption.apply_time.strftime('%Y-%m-%d %H:%M:%S') if adoption.apply_time else None
                    ),
                    'applicant_note': adoption.applicant_note,
                    'admin_note': adoption.admin_note
                }
                
                # 获取宠物信息
                pet = pets.select().where(pets.id == adoption.pet_id).first()
                if pet:
                    pet_data = {
                        'id': pet.id,
                        'name': pet.name,
                        'type': pet.type,
                        'breed': pet.breed,
                        'age': pet.age,
                        'gender': pet.gender,
                        'status': pet.status
                    }
                    # 获取宠物的主照片
                    main_photo = photos.select().where(
                        (photos.pet_id == pet.id) & (photos.is_main == True)
                    ).first()
                    if main_photo:
                        pet_data['image_url'] = f'http://localhost:5000{main_photo.image_url}'
                    adoption_data['pet'] = pet_data
            
                user_adoptions.append(adoption_data)
            
            return jsonify(user_adoptions)
        except Exception as e:
            return jsonify({'error': '获取用户领养申请失败', 'details': str(e)}), 500