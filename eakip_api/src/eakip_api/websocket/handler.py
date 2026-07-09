import logging
from fastapi import WebSocket

logger = logging.getLogger(__name__)

class NotificationWebSocketHandler:
    def __init__(self):
        # Maps connection object references to websocket instances
        self.active_connections: list[WebSocket] = []

    async def connect(self, websocket: WebSocket):
        await websocket.accept()
        self.active_connections.append(websocket)
        logger.info("WebSocket connection established.")
        try:
            await websocket.send_text('{"status":"CONNECTED","message":"Notification feed active"}')
        except Exception as e:
            logger.error(f"Error sending connection status message: {str(e)}")

    def disconnect(self, websocket: WebSocket):
        if websocket in self.active_connections:
            self.active_connections.remove(websocket)
        logger.info("WebSocket connection closed.")

    async def handle_message(self, websocket: WebSocket, data: str):
        logger.info(f"Received message on WebSocket: {data}")

    async def send_notification_to_all(self, json_payload: str):
        for connection in list(self.active_connections):
            try:
                await connection.send_text(json_payload)
            except Exception as e:
                logger.error(f"Failed to send socket notification: {str(e)}")
                # Connection is dead, unregister it
                self.disconnect(connection)

# Global singleton handler instance
notification_handler = NotificationWebSocketHandler()
