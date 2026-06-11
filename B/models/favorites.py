from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class favorites(BaseModel):
    id = AutoField(primary_key=True)
    pet_id = IntegerField()
    user_id = IntegerField()
    like_time = DateTimeField()

    @classmethod
    def get_by_pet(cls, pet_id: int):
        return list(cls.select().where(cls.pet_id == pet_id))

    @classmethod
    def get_by_user(cls, user_id: int):
        return list(cls.select().where(cls.user_id == user_id))

    @classmethod
    def get_by_pet_user(cls, pet_id: int, user_id: int):
        return cls.select().where(cls.pet_id == pet_id and cls.user_id == user_id).first()
