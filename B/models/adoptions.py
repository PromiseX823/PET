from peewee import CharField, IntegerField, TextField, DateTimeField, BooleanField, AutoField
from .base import BaseModel

class adoptions(BaseModel):
    id = AutoField(primary_key=True)
    pet_id = IntegerField()
    applicant_id = IntegerField()
    apply_time = DateTimeField()
    review_time = DateTimeField(null=True)
    status = CharField(max_length=10, default='待审核')
    applicant_note = TextField(null=True)
    admin_note = TextField(null=True)
    contact_method = CharField(max_length=50, null=True)
    interview_time = DateTimeField(null=True)

    @classmethod
    def get_by_pet(cls, pet_id: int):
        return list(cls.select().where(cls.pet_id == pet_id))

    @classmethod
    def get_by_user(cls, applicant_id: int):
        return list(cls.select().where(cls.applicant_id == applicant_id))

    @classmethod
    def get_by_status(cls, status: str):
        return list(cls.select().where(cls.status == status))