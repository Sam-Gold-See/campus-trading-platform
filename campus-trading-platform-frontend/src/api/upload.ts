import request from '@/utils/request'

export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<string>('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}