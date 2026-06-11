from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class pets(BaseModel):
    id = AutoField(primary_key=True)
    name = CharField(max_length=50)
    type = CharField(max_length=10)
    age = IntegerField(null=True)
    breed = CharField(max_length=50, null=True)
    gender = CharField(max_length=10, default='unknown')
    status = CharField(max_length=10, default='待领养')
    description = TextField(null=True)
    health_info = TextField(null=True)
    location = CharField(max_length=100, null=True)
    owner_id = IntegerField()
    weight = IntegerField(null=True)
    color = CharField(max_length=50, null=True)
    neutered = BooleanField(default=False)
    vaccinated = BooleanField(default=False)
    created_at = DateTimeField()
    updated_at = DateTimeField()

    @classmethod
    def get_by_status(cls, status: str):
        return list(cls.select().where(cls.status == status))

    @classmethod
    def get_by_type(cls, pet_type: str):
        return list(cls.select().where(cls.type == pet_type))

    @classmethod
    def get_by_owner(cls, owner_id: int):
        return list(cls.select().where(cls.owner_id == owner_id))