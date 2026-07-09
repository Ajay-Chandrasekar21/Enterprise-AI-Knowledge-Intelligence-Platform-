import os
import json
import logging
import pika

logger = logging.getLogger(__name__)

EXCHANGE_NAME = "eakip.exchange"
DOCUMENT_PROCESSING_ROUTING_KEY = "document.processing.routingkey"
NOTIFICATION_ROUTING_KEY = "notification.routingkey"

class MessageProducer:
    def __init__(self):
        self.amqp_url = os.getenv("RABBITMQ_URL", "amqp://guest:guest@localhost:5672/")

    def _get_channel(self):
        try:
            params = pika.URLParameters(self.amqp_url)
            connection = pika.BlockingConnection(params)
            channel = connection.channel()
            channel.exchange_declare(exchange=EXCHANGE_NAME, exchange_type='topic')
            return connection, channel
        except Exception as e:
            logger.warning(f"Could not connect to RabbitMQ broker: {str(e)}")
            return None, None

    def send_document_job(self, message: any) -> None:
        logger.info(f"Publishing document processing task: {message}")
        conn, ch = self._get_channel()
        if ch:
            try:
                body = json.dumps(message)
                ch.basic_publish(
                    exchange=EXCHANGE_NAME,
                    routing_key=DOCUMENT_PROCESSING_ROUTING_KEY,
                    body=body
                )
                conn.close()
            except Exception as e:
                logger.error(f"Failed publishing document job: {str(e)}")

    def send_notification(self, message: any) -> None:
        logger.info(f"Publishing notification event: {message}")
        conn, ch = self._get_channel()
        if ch:
            try:
                body = json.dumps(message)
                ch.basic_publish(
                    exchange=EXCHANGE_NAME,
                    routing_key=NOTIFICATION_ROUTING_KEY,
                    body=body
                )
                conn.close()
            except Exception as e:
                logger.error(f"Failed publishing notification: {str(e)}")
