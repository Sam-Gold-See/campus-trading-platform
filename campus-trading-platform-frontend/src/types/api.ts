export interface CommonResult<T> {
  code: number
  data: T
  msg: string
}