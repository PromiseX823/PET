from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class notifications(BaseModel):
    id = AutoField(primary_key=True)
    user_id = IntegerField()
    title = CharField(max_length=100)
    content = TextField()
    type = CharField(max_length=10, default='system')
    is_read = BooleanField(default=False)
    related_id = IntegerField(null=True)
    created_at = DateTimeField()

    @classmethod
    def get_by_user(cls, user_id: int):
        return list(cls.select().where(cls.user_id == user_id))

    @classmethod
    def get_unread_by_user(cls, user_id: int):
        return list(cls.select().where((cls.user_id == user_id) & (cls.is_read == False)))

    @classmethod
    def get_by_type(cls, user_id: int, notification_type: str):
        return list(cls.select().where((cls.user_id == user_id) & (cls.type == notification_type)))