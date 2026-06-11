from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class users(BaseModel):
    id = AutoField(primary_key=True)
    username = CharField(max_length=50, unique=True)
    email = CharField(max_length=100, unique=True)
    password = CharField(max_length=255)
    role = CharField(max_length=10, default='user')
    phone = CharField(max_length=20, null=True)
    address = CharField(max_length=255, null=True)
    bio = TextField(null=True)
    avatar_url = CharField(max_length=255, default='default_avatar.png')
    created_at = DateTimeField()
    updated_at = DateTimeField()

    @classmethod
    def get_by_username(cls, username: str):
        return list(cls.select().where(cls.username == username))

    @classmethod
    def get_by_email(cls, email: str):
        return list(cls.select().where(cls.email == email))