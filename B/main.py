from flask import Flask
from flask_cors import CORS
from models.init_db import create_tables
from models.users import users
from models.photos import photos
from models.pets import pets
from models.notifications import notifications
from models.likes import likes
from models.favorites import favorites
from models.comments import comments
from models.adoptions import adoptions
from datetime import datetime
from werkzeug.security import generate_password_hash

# 导入路由模块
from auth import register_auth_routes
from pet_routes import register_pet_routes
from user_routes import register_user_routes
from photo_routes import register_photo_routes
from adoption_routes import register_adoption_routes
from upload_routes import register_upload_routes
from stats_routes import register_stats_routes
from notification_routes import register_notification_routes

# 创建Flask应用
app = Flask(__name__)
# 配置CORS，允许前端跨域访问所有资源，包括静态文件
CORS(app, resources={r"/*": {"origins": "*"}})

# 配置静态文件目录
app.static_folder = 'static'

# 初始化数据库和插入样本数据
def init_data():
    db = create_tables()
    
    # 检查是否已有数据，如果没有则插入
    if users.select().count() == 0:
        # 生成密码哈希
        admin_password = generate_password_hash('123456')
        manager_password = generate_password_hash('123456')
        user_password = generate_password_hash('123456')
        
        users.insert_many([
            {'username': 'admin', 'email': 'admin@petplatform.com', 'password': admin_password, 'role': 'admin', 'phone': '13800138000', 'address': '福建省福州市', 'bio': '平台管理员，负责审核和管理平台内容', 'avatar_url': '/static/avatars/admin_avatar.jpg', 'created_at': '2024-01-01 00:00:00', 'updated_at': '2024-01-01 00:00:00'},
            {'username': 'manager', 'email': 'manager@petplatform.com', 'password': manager_password, 'role': 'admin', 'phone': '13900139000', 'address': '福建省福州市', 'bio': '平台运营经理', 'avatar_url': '/static/avatars/manager_avatar.jpg', 'created_at': '2024-01-01 00:00:00', 'updated_at': '2024-01-01 00:00:00'},
            
            {'username': '安信', 'email': 'Anxin@example.com', 'password': user_password, 'role': 'user', 'phone': '13800138001', 'address': '北京市海淀区', 'bio': '爱狗人士，家有两只柯基', 'avatar_url': '/static/avatars/user1_avatar.jpg', 'created_at': '2024-01-05 10:00:00', 'updated_at': '2024-01-05 10:00:00'},
            {'username': '小明', 'email': 'XiaoMing@example.com', 'password': user_password, 'role': 'user', 'phone': '13800138002', 'address': '上海市徐汇区', 'bio': '猫咪爱好者，救助过5只流浪猫', 'avatar_url': '/static/avatars/user2_avatar.jpg', 'created_at': '2024-01-06 14:30:00', 'updated_at': '2024-01-06 14:30:00'},
            {'username': 'Arno', 'email': 'Arno@example.com', 'password': user_password, 'role': 'user', 'phone': '13800138003', 'address': '广州市天河区', 'bio': '想领养一只小狗陪伴家人', 'avatar_url': '/static/avatars/user3_avatar.jpg', 'created_at': '2024-01-07 09:15:00', 'updated_at': '2024-01-07 09:15:00'},
            {'username': '秋千', 'email': 'QianYou@example.com', 'password': user_password, 'role': 'user', 'phone': '13800138004', 'address': '陕西省汉中', 'bio': '宠物摄影师，喜欢记录宠物美好瞬间', 'avatar_url': '/static/avatars/user4_avatar.jpg', 'created_at': '2024-01-08 16:45:00', 'updated_at': '2024-01-08 16:45:00'},
            {'username': 'LEO', 'email': 'E@example.com', 'password': user_password, 'role': 'user', 'phone': '13800138005', 'address': '杭州市西湖区', 'bio': '动物保护志愿者', 'avatar_url': '/static/avatars/user5_avatar.jpg', 'created_at': '2024-01-09 11:20:00', 'updated_at': '2024-01-09 11:20:00'}
        ]).execute()
        
        # 插入宠物数据
        pets.insert_many([
            # 狗狗
            {'name': '小黄', 'type': 'dog', 'age': 24, 'breed': '柯基犬', 'gender': 'male', 'status': '待领养', 'description': '活泼可爱的柯基犬，喜欢和人玩耍，已经完成疫苗接种', 'health_info': '已驱虫，已绝育，身体健康', 'location': '北京市朝阳区', 'owner_id': 3, 'weight': 12000, 'color': '黄色', 'neutered': True, 'vaccinated': True, 'created_at': '2024-01-05 10:30:00', 'updated_at': '2024-01-05 10:30:00'},
            {'name': '旺财', 'type': 'dog', 'age': 36, 'breed': '金毛寻回犬', 'gender': 'male', 'status': '待领养', 'description': '温顺聪明的金毛，适合有孩子的家庭', 'health_info': '疫苗接种齐全，有轻微皮肤病正在治疗中', 'location': '上海市徐汇区', 'owner_id': 4, 'weight': 35000, 'color': '金黄色', 'neutered': True, 'vaccinated': True, 'created_at': '2024-01-06 14:45:00', 'updated_at': '2024-01-06 14:45:00'},
            {'name': '可乐', 'type': 'dog', 'age': 12, 'breed': '贵宾犬', 'gender': 'female', 'status': '已预定', 'description': '小巧可爱的贵宾犬，性格温顺', 'health_info': '身体健康，已驱虫', 'location': '广州市天河区', 'owner_id': 5, 'weight': 3500, 'color': '棕色', 'neutered': False, 'vaccinated': True, 'created_at': '2024-01-07 09:30:00', 'updated_at': '2024-01-07 09:30:00'},
            {'name': '大鹏', 'type': 'dog', 'age': 48, 'breed': '中华田园犬', 'gender': 'male', 'status': '已领养', 'description': '忠诚聪明的田园犬，曾是流浪狗', 'health_info': '已绝育，疫苗接种齐全', 'location': '深圳市南山区', 'owner_id': 4, 'weight': 25000, 'color': '黑色', 'neutered': True, 'vaccinated': True, 'created_at': '2024-01-08 16:55:00', 'updated_at': '2024-01-08 16:55:00'},
            
            # 猫咪
            {'name': '咪咪', 'type': 'cat', 'age': 18, 'breed': '英国短毛猫', 'gender': 'female', 'status': '待领养', 'description': '蓝猫，性格温顺，喜欢被抚摸', 'health_info': '已绝育，疫苗接种齐全', 'location': '北京市海淀区', 'owner_id': 3, 'weight': 4500, 'color': '灰色', 'neutered': True, 'vaccinated': True, 'created_at': '2024-01-09 11:35:00', 'updated_at': '2024-01-09 11:35:00'},
            {'name': '橘座', 'type': 'cat', 'age': 30, 'breed': '橘猫', 'gender': 'male', 'status': '待领养', 'description': '典型的橘猫，性格慵懒，爱吃', 'health_info': '已驱虫，未绝育', 'location': '上海市浦东新区', 'owner_id': 4, 'weight': 6000, 'color': '橘色', 'neutered': False, 'vaccinated': True, 'created_at': '2024-01-10 15:20:00', 'updated_at': '2024-01-10 15:20:00'},
            {'name': '布丁', 'type': 'cat', 'age': 10, 'breed': '布偶猫', 'gender': 'female', 'status': '已领养', 'description': '漂亮的布偶猫，性格极其温顺', 'health_info': '已绝育，疫苗接种齐全', 'location': '杭州市西湖区', 'owner_id': 5, 'weight': 5000, 'color': '白色和棕色', 'neutered': True, 'vaccinated': True, 'created_at': '2024-01-11 08:40:00', 'updated_at': '2024-01-11 08:40:00'},
            
            # 其他
            {'name': '兔兔', 'type': 'other', 'age': 6, 'breed': '垂耳兔', 'gender': 'female', 'status': '待领养', 'description': '可爱的垂耳兔，性格温顺', 'health_info': '身体健康，已驱虫', 'location': '广州市越秀区', 'owner_id': 5, 'weight': 1500, 'color': '白色', 'neutered': False, 'vaccinated': True, 'created_at': '2024-01-12 10:25:00', 'updated_at': '2024-01-12 10:25:00'},
            {'name': '仓仓', 'type': 'other', 'age': 4, 'breed': '仓鼠', 'gender': 'male', 'status': '待领养', 'description': '活泼好动的小仓鼠', 'health_info': '身体健康', 'location': '深圳市福田区', 'owner_id': 6, 'weight': 100, 'color': '棕色和白色', 'neutered': False, 'vaccinated': False, 'created_at': '2024-01-13 14:10:00', 'updated_at': '2024-01-13 14:10:00'}
        ]).execute()
        
        # 插入照片数据
        photos.insert_many([
            {'pet_id': 1, 'user_id': 3, 'image_url': '/static/images/pet1_1.jpg', 'thumbnail_url': '/static/images/pet1_1.jpg', 'caption': '小黄在公园玩耍', 'like_count': 25, 'view_count': 150, 'is_main': True},
            {'pet_id': 1, 'user_id': 3, 'image_url': '/static/images/pet1_2.jpg', 'thumbnail_url': '/static/images/pet1_2.jpg', 'caption': '小黄的微笑', 'like_count': 18, 'view_count': 120, 'is_main': False},
            {'pet_id': 2, 'user_id': 4, 'image_url': '/static/images/pet2_1.jpg', 'thumbnail_url': '/static/images/pet2_1.jpg', 'caption': '旺财在海边', 'like_count': 32, 'view_count': 200, 'is_main': True},
            {'pet_id': 2, 'user_id': 4, 'image_url': '/static/images/pet2_2.jpg', 'thumbnail_url': '/static/images/pet2_2.jpg', 'caption': '旺财和它的玩具', 'like_count': 15, 'view_count': 100, 'is_main': False},
            {'pet_id': 3, 'user_id': 5, 'image_url': '/static/images/pet3.jpg', 'thumbnail_url': '/static/images/pet3.jpg', 'caption': '可乐的可爱照片', 'like_count': 22, 'view_count': 130, 'is_main': True},
            {'pet_id': 4, 'user_id': 4, 'image_url': '/static/images/pet4.jpg', 'thumbnail_url': '/static/images/pet4.jpg', 'caption': '大鹏的可爱照片', 'like_count': 20, 'view_count': 100, 'is_main': True},
            {'pet_id': 5, 'user_id': 3, 'image_url': '/static/images/pet5_1.jpg', 'thumbnail_url': '/static/images/pet5_1.jpg', 'caption': '咪咪在窗台上', 'like_count': 45, 'view_count': 250, 'is_main': True},
            {'pet_id': 5, 'user_id': 3, 'image_url': '/static/images/pet5_2.jpg', 'thumbnail_url': '/static/images/pet5_2.jpg', 'caption': '咪咪在睡觉', 'like_count': 28, 'view_count': 180, 'is_main': False},
            {'pet_id': 6, 'user_id': 4, 'image_url': '/static/images/pet6_1.jpg', 'thumbnail_url': '/static/images/pet6_1.jpg', 'caption': '橘座在晒太阳', 'like_count': 30, 'view_count': 160, 'is_main': True},
            {'pet_id': 7, 'user_id': 5, 'image_url': '/static/images/pet7.jpg', 'thumbnail_url': '/static/images/pet7.jpg', 'caption': '布丁的可爱照片', 'like_count': 35, 'view_count': 180, 'is_main': True},
            {'pet_id': 8, 'user_id': 5, 'image_url': '/static/images/pet8_1.jpg', 'thumbnail_url': '/static/images/pet8_1.jpg', 'caption': '兔兔吃胡萝卜', 'like_count': 12, 'view_count': 80, 'is_main': True},
            {'pet_id': 9, 'user_id': 6, 'image_url': '/static/images/pet9.jpg', 'thumbnail_url': '/static/images/pet9.jpg', 'caption': '仓仓的可爱照片', 'like_count': 15, 'view_count': 90, 'is_main': True},
        ]).execute()
        
        # 插入认养申请数据
        adoptions.insert_many([
            # 待审核的申请
            {'pet_id': 1, 'applicant_id': 6, 'status': 'pending', 'apply_time': '2024-01-10 10:30:00', 'applicant_note': '我家有个大院子，适合狗狗活动，家里人都很喜欢小动物', 'admin_note': ''},
            {'pet_id': 2, 'applicant_id': 7, 'status': 'pending', 'apply_time': '2024-01-11 14:20:00', 'applicant_note': '我一直想养一只金毛，有养狗经验', 'admin_note': ''},
            
            # 已通过的申请
            {'pet_id': 3, 'applicant_id': 6, 'status': 'approved', 'apply_time': '2024-01-05 09:15:00', 'applicant_note': '已经做好了充分的准备，会给狗狗最好的生活', 'admin_note': '申请已批准', 'review_time': '2024-01-07 16:00:00'},
            
            # 已拒绝的申请
            {'pet_id': 1, 'applicant_id': 5, 'status': 'rejected', 'apply_time': '2024-01-08 11:45:00', 'applicant_note': '想领养一只小狗', 'admin_note': '申请未通过', 'review_time': '2024-01-09 10:30:00'}
        ]).execute()
        
        # 插入评论数据
        comments.insert_many([
            # 普通评论
            {'photo_id': 1, 'user_id': 4, 'content': '好可爱的小柯基！', 'comment_time': '2024-01-10 14:30:00', 'parent_id': None},
            {'photo_id': 1, 'user_id': 5, 'content': '我家也有一只柯基，可以交个朋友', 'comment_time': '2024-01-10 15:45:00', 'parent_id': None},
            {'photo_id': 1, 'user_id': 6, 'content': '请问可以领养吗？', 'comment_time': '2024-01-10 16:20:00', 'parent_id': None},
            {'photo_id': 3, 'user_id': 3, 'content': '金毛真是天使狗狗', 'comment_time': '2024-01-09 10:30:00', 'parent_id': None},
            {'photo_id': 3, 'user_id': 7, 'content': '我也想领养一只金毛', 'comment_time': '2024-01-09 11:15:00', 'parent_id': None},
            {'photo_id': 5, 'user_id': 4, 'content': '蓝猫好漂亮！', 'comment_time': '2024-01-08 16:45:00', 'parent_id': None},
            {'photo_id': 5, 'user_id': 6, 'content': '猫咪看起来好温顺', 'comment_time': '2024-01-08 17:20:00', 'parent_id': None},
            {'photo_id': 5, 'user_id': 7, 'content': '我家也有一只蓝猫', 'comment_time': '2024-01-08 18:30:00', 'parent_id': None},
            
            # 回复评论
            {'photo_id': 1, 'user_id': 3, 'content': '谢谢喜欢！', 'parent_id': 1, 'comment_time': '2024-01-10 14:45:00'},
            {'photo_id': 1, 'user_id': 3, 'content': '可以啊，加我微信交流', 'parent_id': 2, 'comment_time': '2024-01-10 16:00:00'}
        ]).execute()
        
        # 插入点赞数据
        likes.insert_many([
            {'photo_id': 1, 'user_id': 4},
            {'photo_id': 1, 'user_id': 5},
            {'photo_id': 1, 'user_id': 6},
            {'photo_id': 1, 'user_id': 7},
            {'photo_id': 3, 'user_id': 3},
            {'photo_id': 3, 'user_id': 5},
            {'photo_id': 3, 'user_id': 6},
            {'photo_id': 3, 'user_id': 7},
            {'photo_id': 5, 'user_id': 4},
            {'photo_id': 5, 'user_id': 6},
            {'photo_id': 5, 'user_id': 7},
            {'photo_id': 6, 'user_id': 3},
            {'photo_id': 6, 'user_id': 4},
            {'photo_id': 6, 'user_id': 6}
        ]).execute()
        
        # 更新照片的点赞数
        for photo in photos.select():
            like_count = likes.select().where(likes.photo_id == photo.id).count()
            photos.update({'like_count': like_count}).where(photos.id == photo.id).execute()
        
        # 插入收藏数据
        favorites.insert_many([
            {'pet_id': 1, 'user_id': 6, 'like_time': '2024-01-12 10:00:00'},
            {'pet_id': 1, 'user_id': 7, 'like_time': '2024-01-13 14:30:00'},
            {'pet_id': 2, 'user_id': 5, 'like_time': '2024-01-11 09:15:00'},
            {'pet_id': 2, 'user_id': 6, 'like_time': '2024-01-12 16:45:00'},
            {'pet_id': 5, 'user_id': 4, 'like_time': '2024-01-10 11:20:00'},
            {'pet_id': 5, 'user_id': 6, 'like_time': '2024-01-11 15:50:00'},
            {'pet_id': 5, 'user_id': 7, 'like_time': '2024-01-12 08:30:00'},
            {'pet_id': 8, 'user_id': 6, 'like_time': '2024-01-13 10:15:00'}
        ]).execute()
        
        # 插入通知数据
        notifications.insert_many([
            {'user_id': 3, 'type': 'adoption_request', 'content': '用户Arno申请领养您的宠物"小黄"', 'related_id': 1, 'is_read': False, 'created_at': '2024-01-10 10:30:00'},
            {'user_id': 4, 'type': 'comment', 'content': '用户秋千评论了您的照片"小黄在海边"', 'related_id': 4, 'is_read': False, 'created_at': '2024-01-10 14:30:00'},
            {'user_id': 3, 'type': 'like', 'content': '用户Arno点赞了您的照片"小黄在公园玩耍"', 'related_id': 1, 'is_read': False, 'created_at': '2024-01-10 10:00:00'},
            {'user_id': 6, 'type': 'adoption_approved', 'content': '您的认养申请已通过审核', 'related_id': 3, 'is_read': False, 'created_at': '2024-01-07 16:00:00'}
        ]).execute()

# 注册所有路由
register_auth_routes(app)
register_pet_routes(app)
register_user_routes(app)
register_photo_routes(app)
register_adoption_routes(app)
register_upload_routes(app)
register_stats_routes(app)
register_notification_routes(app)

if __name__ == '__main__':
    print('初始化应用...')
    # 初始化数据库
    init_data()
    # 启动Flask应用
    app.run(host='0.0.0.0', port=5000, debug=True)





