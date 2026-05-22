type MessageHandler = (data: any) => void

class WebSocketClient {
  private ws: WebSocket | null = null
  private url: string
  private reconnectTimer: ReturnType<typeof setTimeout> | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectInterval = 3000
  private handlers: Map<string, MessageHandler[]> = new Map()
  private messageQueue: string[] = []

  constructor() {
    this.url = import.meta.env.VITE_WS_BASE_URL
  }

  connect() {
    if (this.ws?.readyState === WebSocket.OPEN) return

    const token = localStorage.getItem('token')
    if (!token) return

    this.ws = new WebSocket(`${this.url}?token=${token}`)

    this.ws.onopen = () => {
      this.reconnectAttempts = 0
      this.flushQueue()
    }

    this.ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        const type = data.type || 'default'
        this.handlers.get(type)?.forEach((handler) => handler(data))
      } catch {
        this.handlers.get('default')?.forEach((handler) => handler(event.data))
      }
    }

    this.ws.onclose = () => {
      this.tryReconnect()
    }

    this.ws.onerror = () => {
      this.ws?.close()
    }
  }

  disconnect() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer)
      this.reconnectTimer = null
    }
    this.reconnectAttempts = this.maxReconnectAttempts
    this.ws?.close()
    this.ws = null
  }

  send(data: object) {
    const message = JSON.stringify(data)
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(message)
    } else {
      this.messageQueue.push(message)
    }
  }

  on(type: string, handler: MessageHandler) {
    if (!this.handlers.has(type)) {
      this.handlers.set(type, [])
    }
    this.handlers.get(type)!.push(handler)
  }

  off(type: string, handler: MessageHandler) {
    const handlers = this.handlers.get(type)
    if (handlers) {
      const index = handlers.indexOf(handler)
      if (index > -1) handlers.splice(index, 1)
    }
  }

  private tryReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) return
    this.reconnectTimer = setTimeout(() => {
      this.reconnectAttempts++
      this.connect()
    }, this.reconnectInterval)
  }

  private flushQueue() {
    while (this.messageQueue.length > 0) {
      const message = this.messageQueue.shift()!
      this.ws?.send(message)
    }
  }
}

export const wsClient = new WebSocketClient()