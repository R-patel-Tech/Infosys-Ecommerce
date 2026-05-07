import axiosClient from '../api/axiosClient.js'

export async function get(path) {
  const response = await axiosClient.get(path)
  return response.data
}

export async function post(path, payload) {
  const response = await axiosClient.post(path, payload)
  return response.data
}

export async function put(path, payload) {
  const response = await axiosClient.put(path, payload)
  return response.data
}

export async function del(path, config = {}) {
  const response = await axiosClient.delete(path, {
    withCredentials: false,
    ...config,
  })
  return response.data
}
