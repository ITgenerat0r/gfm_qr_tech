from django.contrib import admin

# Register your models here.
from .models import Decimals, Devices, Operations, Sessions, UserGroups, WgBonds, Workers

admin.site.register(Decimals)
admin.site.register(Devices)
admin.site.register(Operations)
admin.site.register(Sessions)
admin.site.register(UserGroups)
admin.site.register(WgBonds)
admin.site.register(Workers)