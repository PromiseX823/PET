from .base import init_db
from .users import users
from .photos import photos
from .pets import pets
from .notifications import notifications
from .likes import likes
from .favorites import favorites
from .photo_collections import photo_collections
from .comments import comments
from .adoptions import adoptions


def create_tables():
    db = init_db()
    # 只创建不存在的表，不删除现有表
    db.create_tables([users, photos, pets, notifications, likes, favorites, photo_collections, comments, adoptions], safe=True)
    print("数据库表检查完成！")
    return db


if __name__ == "__main__":
    create_tables()