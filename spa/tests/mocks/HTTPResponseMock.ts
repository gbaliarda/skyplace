export class HeadersMock {
  private headers: { [key: string]: string } = {}

  public set(key: string, value: string) {
    this.headers[key] = value
  }

  public get(key: string) {
    return this.headers[key]
  }
}

export default function mockHTTPResponse(
  status: number,
  body?: Object,
  // @ts-ignore
  headers?: HeadersMock = new HeadersMock(),
) {
  return (global.fetch = jest.fn().mockImplementationOnce(
    () =>
      new Promise((resolve, reject) =>
        resolve({
          ok: status > 199 && status < 300,
          status,
          headers: headers,
          json: () => new Promise((res, rej) => res(body)),
        }),
      ),
  ))
}
