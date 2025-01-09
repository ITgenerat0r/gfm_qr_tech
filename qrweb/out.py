# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Decimals(models.Model):
    num = models.CharField(unique=True, max_length=32, blank=True, null=True)
    d_name = models.CharField(max_length=256, blank=True, null=True)
    d_type = models.CharField(max_length=128, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'decimals'


class Devices(models.Model):
    serial_number = models.IntegerField(primary_key=True)
    decimal = models.ForeignKey(Decimals, models.DO_NOTHING, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'devices'


class Operations(models.Model):
    serial_number = models.IntegerField()
    operation = models.CharField(max_length=128)
    worker = models.CharField(max_length=256)
    dt = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'operations'


class Sessions(models.Model):
    iv = models.CharField(max_length=256, blank=True, null=True)
    aes_key = models.CharField(max_length=256, blank=True, null=True)
    date_last_conn = models.DateTimeField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'sessions'


class UserGroups(models.Model):
    g_name = models.CharField(unique=True, max_length=256)
    access = models.IntegerField()

    class Meta:
        managed = False
        db_table = 'user_groups'


class WgBonds(models.Model):
    w_login = models.IntegerField()
    g_name = models.IntegerField()

    class Meta:
        managed = False
        db_table = 'wg_bonds'


class Workers(models.Model):
    w_login = models.CharField(unique=True, max_length=256)
    w_passhash = models.CharField(max_length=256)
    w_name = models.CharField(max_length=256)

    class Meta:
        managed = False
        db_table = 'workers'
