import logging
from typing import List
from .runtime import AgentResult

logger = logging.getLogger(__name__)

class ConsensusEngine:
    @staticmethod
    def resolve_consensus(results: List[AgentResult]) -> str:
        logger.info("Merging multiple agents responses using ConsensusEngine rank filters")
        
        if not results:
            return "No responses to resolve"

        # Rank by confidence score descending
        ranked = sorted(results, key=lambda r: r.confidence, reverse=True)

        merged_parts = ["Consensus Resolution (Ranks by confidence):\n\n"]
        for idx, res in enumerate(ranked):
            merged_parts.append(
                f"[{idx + 1}] Agent: {res.agent_name} (Confidence: {res.confidence:.2f})\n"
                f"{res.output_content}\n\n"
            )

        return "".join(merged_parts)
