from django.urls import path

from . import views

app_name = 'qr'
urlpatterns = [
path('get_decimals', views.get_decimals, name='get_decimals'),
path('', views.index, name = 'index')
]
