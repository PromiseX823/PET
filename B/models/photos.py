from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class photos(BaseModel):
    id = AutoField(primary_key=True)
    pet_id = IntegerField()
    user_id = IntegerField()
    image_url = CharField(max_length=255)
    thumbnail_url = CharField(max_length=255, null=True)
    caption = CharField(max_length=255, null=True)
    like_count = IntegerField(default=0)
    view_count = IntegerField(default=0)
    upload_time = DateTimeField()
    is_approved = BooleanField(default=True)
    is_main = BooleanField(default=False)

    @classmethod
    def get_by_pet(cls, pet_id: int):
        return list(cls.select().where(cls.pet_id == pet_id))

    @classmethod
    def get_by_user(cls, user_id: int):
        return list(cls.select().where(cls.user_id == user_id))

    @classmethod
    def get_main_photo(cls, pet_id: int):
        return list(cls.select().where(cls.pet_id == pet_id and cls.is_main == True))