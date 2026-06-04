import axiosClient from '../api/axiosClient.js'

export async function get(path, config = {}) {
  const response = await axiosClient.get(path, config)
  return response.data
}

export async function post(path, payload, config = {}) {
  const response = await axiosClient.post(path, payload, config)
  return response.data
}

export async function put(path, payload, config = {}) {
  const response = await axiosClient.put(path, payload, config)
  return response.data
}

export async function del(path, config = {}) {
  const response = await axiosClient.delete(path, {
    withCredentials: false,
    ...config,
  })
  return response.data
}
