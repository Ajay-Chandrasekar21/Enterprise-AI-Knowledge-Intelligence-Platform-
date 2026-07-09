import logging

logger = logging.getLogger(__name__)

class DocumentParser:
    @staticmethod
    def parse(input_stream) -> str:
        logger.info("Starting multi-format document parsing using Apache Tika AutoDetectParser")
        try:
            # Try lazy import of tika to avoid strict dependencies at startup
            try:
                from tika import parser
                parsed = parser.from_buffer(input_stream.read())
                content = parsed.get("content", "")
                if content:
                    logger.info(f"Document parsed successfully using Tika. Content length: {len(content)}")
                    return content
            except Exception as ex:
                logger.warning(f"Tika parsing failed or library not present, using stream fallback: {str(ex)}")
            
            # Reset stream if possible and read as string
            if hasattr(input_stream, "seek"):
                input_stream.seek(0)
            data = input_stream.read()
            if isinstance(data, bytes):
                content = data.decode("utf-8", errors="ignore")
            else:
                content = str(data)
                
            logger.info(f"Document parsed successfully using fallback. Content length: {len(content)}")
            return content
        except Exception as e:
            logger.error("Parsing failed with exception", exc_info=e)
            raise RuntimeError(f"Failed to parse document content: {str(e)}") from e
