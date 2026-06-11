import requests

# 测试第一页
print('测试第一页 (page=1, page_size=10):')
response = requests.get('http://localhost:5000/api/pets', params={'page': 1, 'page_size': 10})
if response.status_code == 200:
    data = response.json()
    print(f'状态码: {response.status_code}')
    print(f'页码: {data.get("page")}')
    print(f'每页大小: {data.get("page_size")}')
    print(f'总宠物数: {data.get("total")}')
    print(f'总页数: {data.get("total_pages")}')
    print(f'返回宠物数: {len(data.get("pets", []))}')
    print('宠物列表:')
    for pet in data.get('pets', []):
        print(f'  - {pet.get("name")} (ID: {pet.get("id")}, 状态: {pet.get("status")})')
else:
    print(f'请求失败: {response.status_code}')
    print(f'响应内容: {response.text}')

print('\n' + '='*50 + '\n')

# 测试第二页
print('测试第二页 (page=2, page_size=10):')
response = requests.get('http://localhost:5000/api/pets', params={'page': 2, 'page_size': 10})
if response.status_code == 200:
    data = response.json()
    print(f'状态码: {response.status_code}')
    print(f'页码: {data.get("page")}')
    print(f'每页大小: {data.get("page_size")}')
    print(f'总宠物数: {data.get("total")}')
    print(f'总页数: {data.get("total_pages")}')
    print(f'返回宠物数: {len(data.get("pets", []))}')
    print('宠物列表:')
    for pet in data.get('pets', []):
        print(f'  - {pet.get("name")} (ID: {pet.get("id")}, 状态: {pet.get("status")})')
else:
    print(f'请求失败: {response.status_code}')
    print(f'响应内容: {response.text}')
