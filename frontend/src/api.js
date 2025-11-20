const api = {
    gamesList: '/api/catalog/games',
    createHold: '/api/reservations/holds',
    getHold: (holdId) => `/api/reservations/holds/${holdId}`,
    createOrder: '/api/orders',
    cancelOrder: (orderId) => `/api/orders/${orderId}/cancel`
}

async function request(url, opts = {}) {
    const res = await fetch(url, {
        headers: {'Content-Type': 'application/json'},
        ...opts
    })
    if (!res.ok) {
        const text = await res.text()
        throw new Error(text || res.statusText)
    }
    return res.json().catch(() => null)
}

export const getGames = () => request(api.gamesList)
export const createHold = (body) =>
    request(api.createHold, {method: 'POST', body: JSON.stringify(body)})
export const getHold = (id) => request(api.getHold(id))
export const createOrder = (body) =>
    request(api.createOrder, {method: 'POST', body: JSON.stringify(body)})
export const cancelOrder = (orderId) =>
    request(api.cancelOrder(orderId), {method: 'POST'})
