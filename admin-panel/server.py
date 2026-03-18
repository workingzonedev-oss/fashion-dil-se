from __future__ import annotations

import json
import uuid
import re
import base64
from datetime import datetime, timezone
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from typing import Any


ROOT = Path(__file__).resolve().parent
STORE_PATH = ROOT / "data" / "store.json"
UPLOADS_DIR = ROOT / "uploads"
DEFAULT_STORE = {
    "updatedAt": "",
    "campaigns": [],
    "discoverItems": [],
    "products": [],
    "offers": [],
    "notifications": [],
    "orders": [],
}


def ensure_store() -> None:
    STORE_PATH.parent.mkdir(parents=True, exist_ok=True)
    UPLOADS_DIR.mkdir(parents=True, exist_ok=True)
    if not STORE_PATH.exists():
        STORE_PATH.write_text(json.dumps(DEFAULT_STORE, indent=2), encoding="utf-8")


def load_store() -> dict[str, Any]:
    ensure_store()
    try:
        return json.loads(STORE_PATH.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        STORE_PATH.write_text(json.dumps(DEFAULT_STORE, indent=2), encoding="utf-8")
        return DEFAULT_STORE.copy()


def save_store(store: dict[str, Any]) -> dict[str, Any]:
    normalized = DEFAULT_STORE | store
    normalized["updatedAt"] = datetime.now(timezone.utc).isoformat()
    STORE_PATH.write_text(json.dumps(normalized, indent=2), encoding="utf-8")
    return normalized


MIME_MAP = {
    "image/png": ".png",
    "image/jpeg": ".jpg",
    "image/webp": ".webp",
    "image/gif": ".gif",
    "image/svg+xml": ".svg",
}


class AdminHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args: Any, **kwargs: Any) -> None:
        super().__init__(*args, directory=str(ROOT), **kwargs)

    def end_headers(self) -> None:
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.send_header("Cache-Control", "no-store")
        super().end_headers()

    def do_OPTIONS(self) -> None:
        self.send_response(204)
        self.end_headers()

    def do_GET(self) -> None:
        if self.path == "/api/store":
            self._send_json(load_store())
            return
        if self.path.startswith("/uploads/"):
            super().do_GET()
            return
        super().do_GET()

    def do_POST(self) -> None:
        if self.path == "/api/store":
            payload = self._read_json_body()
            if payload is None:
                return
            self._send_json(save_store(payload))
            return

        if self.path == "/api/orders":
            payload = self._read_json_body()
            if payload is None:
                return
            store = load_store()
            orders = store.get("orders", [])
            orders.insert(0, payload)
            store["orders"] = orders
            self._send_json(save_store(store))
            return

        if self.path == "/api/upload":
            self._handle_upload()
            return

        self.send_error(404, "Unknown API route")

    def do_PUT(self) -> None:
        if self.path == "/api/store":
            payload = self._read_json_body()
            if payload is None:
                return
            self._send_json(save_store(payload))
            return
        self.send_error(404, "Unknown API route")

    def _handle_upload(self) -> None:
        try:
            length = int(self.headers.get("Content-Length", "0"))
            raw = self.rfile.read(length).decode("utf-8")
            body = json.loads(raw) if raw else {}

            data_url = body.get("image", "")
            match = re.match(r"data:(image/[a-z+]+);base64,(.+)", data_url, re.DOTALL)
            if not match:
                self.send_error(400, "Invalid image data")
                return

            mime_type = match.group(1)
            image_data = base64.b64decode(match.group(2))
            ext = MIME_MAP.get(mime_type, ".png")
            filename = f"{uuid.uuid4().hex[:12]}{ext}"
            filepath = UPLOADS_DIR / filename
            filepath.write_bytes(image_data)

            self._send_json({"url": f"/uploads/{filename}", "filename": filename})
        except Exception as exc:
            self.send_error(500, f"Upload failed: {exc}")

    def _read_json_body(self) -> dict[str, Any] | None:
        try:
            length = int(self.headers.get("Content-Length", "0"))
            raw = self.rfile.read(length).decode("utf-8")
            return json.loads(raw) if raw else {}
        except (ValueError, json.JSONDecodeError):
            self.send_error(400, "Invalid JSON body")
            return None

    def _send_json(self, payload: dict[str, Any]) -> None:
        body = json.dumps(payload).encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


if __name__ == "__main__":
    ensure_store()
    server = ThreadingHTTPServer(("127.0.0.1", 4173), AdminHandler)
    print("Fashion Dil Se admin server running at http://127.0.0.1:4173")
    server.serve_forever()
