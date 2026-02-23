const DEFAULT_HEADERS = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
}

async function handleResponse(response) {
    if (response.status === 204) return null

    const contentType = response.headers.get('Content-Type') || ''
    const text = await response.text()

    if (!response.ok) {
        if (contentType.includes('application/json')) {
            try {
                const body = JSON.parse(text)
                const msg = body.message || body.error || JSON.stringify(body)
                throw new Error(msg)
            } catch (e) {
                throw new Error(text || response.statusText)
            }
        } else {
            throw new Error(text || response.statusText)
        }
    }

    if (!text) return null

    if (contentType.includes('application/json')) {
        return JSON.parse(text)
    }

    return text
}

function buildUrl(path, params) {
    if (!params) return path
    const qs = new URLSearchParams(params).toString()
    return qs ? `${path}?${qs}` : path
}

async function request(path, {method = 'GET', body = null, headers = {}, params = null, idempotencyKey = null} = {}) {
    const url = buildUrl(path, params)
    const opts = {
        method,
        headers: {...DEFAULT_HEADERS, ...headers},
    }

    if (idempotencyKey) {
        opts.headers['Idempotency-Key'] = idempotencyKey
    }

    if (body != null) {
        opts.body = typeof body === 'string' ? body : JSON.stringify(body)
    }

    const res = await fetch(url, opts)
    return handleResponse(res)
}

export const getGames = () => request('/api/catalog/games')

export const getGame = (gameId) => request(`/api/catalog/games/${gameId}`)


export const createHold = (body, idempotencyKey = null) =>
    request('/api/reservations/holds', {method: 'POST', body, idempotencyKey})

export const getHold = (holdId) => request(`/api/reservations/holds/${holdId}`)

export const cancelHold = (holdId) =>
    request(`/api/reservations/holds/${holdId}`, {method: 'DELETE'})

export const createOrder = (body, idempotencyKey = null) =>
    request('/api/orders', {method: 'POST', body, idempotencyKey})

export const getOrder = (orderId) => request(`/api/orders/${orderId}`)

export const cancelOrder = (orderId) =>
    request(`/api/orders/${orderId}/cancel`, {method: 'POST'})


export const listOrders = (params) => request('/api/orders', {params})

// User API
export const getUsers = () => request('/api/users')

export const getUser = (userId) => request(`/api/users/${userId}`)

export const getUserByUsername = (username) => request(`/api/users/username/${username}`)

export const createUser = (body) => request('/api/users', {method: 'POST', body})

export const updateUser = (userId, body) => request(`/api/users/${userId}`, {method: 'PUT', body})

export const deleteUser = (userId) => request(`/api/users/${userId}`, {method: 'DELETE'})

// User Orders API
export const getUserOrders = (userId) => request(`/api/user-orders/user/${userId}`)

export const getUserOrder = (orderId) => request(`/api/user-orders/${orderId}`)

export const createUserOrder = (body) => request('/api/user-orders', {method: 'POST', body})

export const updateUserOrderStatus = (orderId, status) => 
    request(`/api/user-orders/${orderId}/status?status=${status}`, {method: 'PUT'})

export const cancelUserOrder = (orderId) => 
    request(`/api/user-orders/${orderId}/cancel`, {method: 'POST'})

const api = {
    getGames,
    getGame,
    createHold,
    getHold,
    cancelHold,
    createOrder,
    getOrder,
    cancelOrder,
    listOrders,
    // User API
    getUsers,
    getUser,
    getUserByUsername,
    createUser,
    updateUser,
    deleteUser,
    // User Orders API
    getUserOrders,
    getUserOrder,
    createUserOrder,
    updateUserOrderStatus,
    cancelUserOrder
}

export default api
