from models.base import init_db, db
from models.pets import pets
from models.photos import photos
from models.users import users
from models.comments import comments
from models.likes import likes
from models.favorites import favorites
from models.adoptions import adoptions
from models.notifications import notifications
from models.photo_collections import photo_collections

# 初始化数据库
print('初始化数据库连接...')
database = init_db()

# 创建表（如果不存在）
print('创建表（如果不存在）...')
db.create_tables([
    users, pets, photos, comments, likes, 
    favorites, adoptions, notifications, photo_collections
], safe=True)

# 查询宠物总数
print('\n查询宠物数据...')
total_pets = pets.select().count()
print(f'宠物总数: {total_pets}')

# 查询不同状态的宠物数
statuses = ['待领养', '已领养', '已预定', '已拒绝', '待审核']
for status in statuses:
    count = pets.select().where(pets.status == status).count()
    print(f'{status}: {count}')

# 列出所有宠物
print('\n所有宠物:')
for pet in pets.select():
    print(f'ID: {pet.id}, 名称: {pet.name}, 状态: {pet.status}')

# 关闭数据库连接
database.close()
print('\n数据库连接已关闭')
