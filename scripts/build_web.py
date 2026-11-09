"""Собирает автономную веб-версию Mystic Tarot.

Данные раскладов и колоды берутся из локального API и встраиваются в HTML,
чтобы страница работала без сервера и могла быть опубликована на GitHub Pages.
"""

from __future__ import annotations

import json
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
TEMPLATE = ROOT / "web" / "index.template.html"
TARGETS = (
    ROOT / "backend" / "static" / "index.html",
    ROOT / "docs" / "index.html",
)
API = "http://127.0.0.1:8000/api/v1"


def fetch(path: str):
    with urllib.request.urlopen(f"{API}{path}", timeout=30) as response:
        return json.load(response)


def to_js(value) -> str:
    raw = json.dumps(value, ensure_ascii=False, separators=(",", ":"))
    return raw.replace("</", "<\\/")


def main() -> None:
    spreads = [
        {
            "id": item["id"],
            "title": item["title"],
            "description": item["description"],
            "positions": item["positions"],
        }
        for item in fetch("/tarot/spreads")
    ]
    cards = fetch("/tarot/cards")

    template = TEMPLATE.read_text(encoding="utf-8")
    html = template.replace("__SPREADS__", to_js(spreads)).replace("__CARDS__", to_js(cards))

    for target in TARGETS:
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text(html, encoding="utf-8")
        print(f"wrote {target} ({target.stat().st_size} bytes)")

    (ROOT / "docs" / ".nojekyll").write_text("", encoding="utf-8")
    print(f"spreads: {len(spreads)}, cards: {len(cards)}")


if __name__ == "__main__":
    main()
