from typing import List

class SmartChunkGenerator:
    @staticmethod
    def generate_chunks(text: str, chunk_size: int, overlap: int) -> List[str]:
        chunks = []
        if not text or not text.strip():
            return chunks

        text_length = len(text)
        start = 0

        while start < text_length:
            end = min(start + chunk_size, text_length)
            chunk = text[start:end]
            chunks.append(chunk)

            if end == text_length:
                break
            start += (chunk_size - overlap)

        return chunks
