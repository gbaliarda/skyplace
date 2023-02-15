export default class LocalStorageMock {
  private store: Record<string, string | number> = {}

  clear() {
    this.store = {}
  }

  getItem(key: string) {
    return this.store[key] || null
  }

  setItem(key: string, value: string | number) {
    this.store[key] = String(value)
  }

  removeItem(key: string) {
    delete this.store[key]
  }
}

// @ts-ignore
global.localStorage = new LocalStorageMock()
