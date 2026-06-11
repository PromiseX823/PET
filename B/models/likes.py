from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class likes(BaseModel):
    id = AutoField(primary_key=True)
    photo_id = IntegerField()
    user_id = IntegerField()
    like_time = DateTimeField()

    @classmethod
    def get_by_photo(cls, photo_id: int):
        return list(cls.select().where(cls.photo_id == photo_id))

    @classmethod
    def get_by_user(cls, user_id: int):
        return list(cls.select().where(cls.user_id == user_id))

    @classmethod
    def get_by_photo_user(cls, photo_id: int, user_id: int):
        return cls.select().where(cls.photo_id == photo_id and cls.user_id == user_id).first()
