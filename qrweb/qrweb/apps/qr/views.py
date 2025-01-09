from django.shortcuts import render
from django.http import Http404, FileResponse

# Create your views here.

from .forms import *
from .models import *




def index(request):
	# return render(request, 'qr/index.html')
	# numbers = Decimals.objects.all()
	# print(numbers)
	return render(request, 'qr/qrcode.html')




from rest_framework import serializers
# from rest_framework.response import Response

# from rest_framework.renderers import JSONRenderer

class CapitalSerializer(serializers.Serializer):
    decimal = serializers.CharField(source='num', max_length=32)
    name = serializers.CharField(source='d_name', max_length=255)
    type = serializers.CharField(source='d_type', max_length=255)



def get_decimals(request):
	numbers = Decimals.objects.all().order_by("d_type")
	ser = CapitalSerializer(instance = numbers, many=True)
	data = f"{{'data': {ser.data}}}".replace("'", '"')
	# data = f"{ser.data}".replace("'", '"')

	print("----- DATA ---------------------------------------------")
	print(ser)
	print("........................................................")
	print(ser.data)
	print("--------------------------------------------------------")

	return FileResponse(data, content_type='application/json')


