from flask import jsonify, request
from models.notifications import notifications
from models.users import users
from datetime import datetime


def register_notification_routes(app):
    """注册通知相关的路由"""
    
    @app.route('/api/notifications', methods=['GET'])
    def get_notifications():
        """获取用户的通知列表"""
        try:
            # 获取查询参数
            user_id = request.args.get('user_id')
            is_read = request.args.get('is_read')
            notification_type = request.args.get('type')
            limit = request.args.get('limit', 20)
            offset = request.args.get('offset', 0)
            
            if not user_id:
                return jsonify({'error': '缺少必要的参数: user_id'}), 400
            
            # 构建查询
            query = notifications.select().where(notifications.user_id == user_id)
            
            # 添加筛选条件
            if is_read is not None:
                query = query.where(notifications.is_read == (is_read.lower() == 'true'))
            
            if notification_type:
                query = query.where(notifications.type == notification_type)
            
            # 添加排序和分页
            query = query.order_by(notifications.created_at.desc())
            query = query.limit(int(limit)).offset(int(offset))
            
            # 获取总数量
            total = notifications.select().where(notifications.user_id == user_id).count()
            
            # 构建响应数据
            notification_list = []
            for notification in query:
                notification_data = {
                    'id': notification.id,
                    'user_id': notification.user_id,
                    'title': notification.title,
                    'content': notification.content,
                    'type': notification.type,
                    'is_read': notification.is_read,
                    'related_id': notification.related_id,
                    'created_at': notification.created_at.strftime('%Y-%m-%d %H:%M:%S') if notification.created_at else None
                }
                notification_list.append(notification_data)
            
            return jsonify({
                'success': True,
                'data': notification_list,
                'total': total,
                'limit': int(limit),
                'offset': int(offset)
            }), 200
            
        except Exception as e:
            return jsonify({'error': '获取通知列表失败', 'details': str(e)}), 500
    
    @app.route('/api/notifications/unread', methods=['GET'])
    def get_unread_notifications():
        """获取用户的未读通知"""
        try:
            # 获取查询参数
            user_id = request.args.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少必要的参数: user_id'}), 400
            
            # 查询未读通知
            unread_notifications = notifications.get_unread_by_user(int(user_id))
            
            # 构建响应数据
            notification_list = []
            for notification in unread_notifications:
                notification_data = {
                    'id': notification.id,
                    'user_id': notification.user_id,
                    'title': notification.title,
                    'content': notification.content,
                    'type': notification.type,
                    'is_read': notification.is_read,
                    'related_id': notification.related_id,
                    'created_at': notification.created_at.strftime('%Y-%m-%d %H:%M:%S') if notification.created_at else None
                }
                notification_list.append(notification_data)
            
            return jsonify({
                'success': True,
                'data': notification_list,
                'total': len(notification_list)
            }), 200
            
        except Exception as e:
            return jsonify({'error': '获取未读通知失败', 'details': str(e)}), 500
    
    @app.route('/api/notifications/<int:notification_id>/read', methods=['POST'])
    def mark_notification_as_read(notification_id):
        """标记单个通知为已读"""
        try:
            # 获取用户ID
            data = request.get_json()
            user_id = data.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少必要的参数: user_id'}), 400
            
            # 查找通知并检查是否属于当前用户
            notification = notifications.select().where(
                (notifications.id == notification_id) & 
                (notifications.user_id == user_id)
            ).first()
            
            if not notification:
                return jsonify({'error': '通知不存在'}), 404
            
            # 更新通知状态
            notification.is_read = True
            notification.save()
            
            return jsonify({'success': True, 'message': '通知已标记为已读'}), 200
            
        except Exception as e:
            return jsonify({'error': '标记通知为已读失败', 'details': str(e)}), 500
    
    @app.route('/api/notifications/read-all', methods=['POST'])
    def mark_all_notifications_as_read():
        """标记所有通知为已读"""
        try:
            # 获取查询参数
            user_id = request.get_json().get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少必要的参数: user_id'}), 400
            
            # 更新所有通知状态
            updated_rows = notifications.update({
                'is_read': True
            }).where(notifications.user_id == user_id).execute()
            
            return jsonify({
                'success': True,
                'message': f'成功标记 {updated_rows} 条通知为已读'
            }), 200
            
        except Exception as e:
            return jsonify({'error': '标记所有通知为已读失败', 'details': str(e)}), 500
    
    @app.route('/api/notifications/<int:notification_id>', methods=['DELETE'])
    def delete_notification(notification_id):
        """删除单个通知"""
        try:
            # 获取用户ID
            data = request.get_json()
            user_id = data.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少必要的参数: user_id'}), 400
            
            # 查找通知并检查是否属于当前用户
            notification = notifications.select().where(
                (notifications.id == notification_id) & 
                (notifications.user_id == user_id)
            ).first()
            
            if not notification:
                return jsonify({'error': '通知不存在'}), 404
            
            # 删除通知
            notification.delete_instance()
            
            return jsonify({'success': True, 'message': '通知已删除'}), 200
            
        except Exception as e:
            return jsonify({'error': '删除通知失败', 'details': str(e)}), 500
    
    @app.route('/api/notifications/unread/count', methods=['GET'])
    def get_unread_notification_count():
        """获取用户的未读通知数量"""
        try:
            # 获取查询参数
            user_id = request.args.get('user_id')
            
            if not user_id:
                return jsonify({'error': '缺少必要的参数: user_id'}), 400
            
            # 查询未读通知数量
            count = len(notifications.get_unread_by_user(int(user_id)))
            
            return jsonify({
                'success': True,
                'count': count
            }), 200
            
        except Exception as e:
            return jsonify({'error': '获取未读通知数量失败', 'details': str(e)}), 500
