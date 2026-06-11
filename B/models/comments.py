from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class comments(BaseModel):
    id = AutoField(primary_key=True)
    photo_id = IntegerField()
    user_id = IntegerField()
    content = TextField()
    comment_time = DateTimeField()
    is_deleted = BooleanField(default=False)
    parent_id = IntegerField(null=True)

    @classmethod
    def get_by_photo(cls, photo_id: int):
        return list(cls.select().where(cls.photo_id == photo_id and cls.is_deleted == False))

    @classmethod
    def get_by_user(cls, user_id: int):
        return list(cls.select().where(cls.user_id == user_id and cls.is_deleted == False))

    @classmethod
    def get_by_parent(cls, parent_id: int):
        return list(cls.select().where(cls.parent_id == parent_id and cls.is_deleted == False))
