from peewee import Model, MySQLDatabase, SqliteDatabase, DatabaseProxy
from datetime import datetime
import sys
import os

# 添加项目根目录到Python路径
sys.path.append(os.path.abspath(os.path.dirname(os.path.dirname(__file__))))
from config import DB_BACKEND, MYSQL_CONFIG, SQLITE_CONFIG
import pymysql
pymysql.install_as_MySQLdb()

db = DatabaseProxy()

def init_db():
    if DB_BACKEND == 'mysql':
        # 先尝试连接到MySQL服务器（不指定数据库）
        try:
            import pymysql
            connection = pymysql.connect(
                host=MYSQL_CONFIG['host'],
                user=MYSQL_CONFIG['user'],
                password=MYSQL_CONFIG['password'],
                port=MYSQL_CONFIG['port'],
                charset=MYSQL_CONFIG['charset']
            )
            
            # 创建游标
            cursor = connection.cursor()
            
            # 检查数据库是否存在，如果不存在则创建
            cursor.execute(f"CREATE DATABASE IF NOT EXISTS {MYSQL_CONFIG['database']} CHARACTER SET {MYSQL_CONFIG['charset']}")
            
            # 关闭游标和连接
            cursor.close()
            connection.close()
            
            print(f"数据库 {MYSQL_CONFIG['database']} 检查/创建完成")
        except Exception as e:
            print(f"创建数据库失败: {e}")
            raise
        
        # 连接到指定的数据库
        database = MySQLDatabase(
            MYSQL_CONFIG['database'],
            host=MYSQL_CONFIG['host'],
            user=MYSQL_CONFIG['user'],
            password=MYSQL_CONFIG['password'],
            port=MYSQL_CONFIG['port'],
            charset=MYSQL_CONFIG['charset']
        )
    else:
        database = SqliteDatabase(SQLITE_CONFIG['path'])

    db.initialize(database)
    database.connect()
    return database

class BaseModel(Model):
    class Meta:
        database = db