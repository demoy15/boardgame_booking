import React, {useState} from 'react'
import {cancelOrder, createOrder, getOrder, listOrders} from '../api'

export default function Orders() {
    const [holdId, setHoldId] = useState('')
    const [userId, setUserId] = useState('22222222-2222-2222-2222-222222222222')
    const [orderId, setOrderId] = useState('')
    const [orders, setOrders] = useState([])
    const [msg, setMsg] = useState(null)

    const makeOrder = async (e) => {
        e.preventDefault()
        setMsg(null)
        try {
            const resp = await createOrder({holdId, userId})
            setOrderId(resp.orderId)
            setMsg('Order created: ' + resp.orderId)
        } catch (err) {
            setMsg('Error: ' + err.message)
        }
    }

    const doCancel = async () => {
        if (!orderId) return
        try {
            await cancelOrder(orderId)
            setMsg('Order cancelled: ' + orderId)
            setOrderId('')
        } catch (err) {
            setMsg('Error: ' + err.message)
        }
    }

    const loadOrder = async () => {
        if (!orderId) return
        setMsg(null)
        try {
            const order = await getOrder(orderId)
            setOrders([order])
        } catch (err) {
            setMsg('Error: ' + err.message)
        }
    }

    const loadOrders = async () => {
        setMsg(null)
        try {
            const items = await listOrders(userId ? {userId} : undefined)
            setOrders(items || [])
        } catch (err) {
            setMsg('Error: ' + err.message)
        }
    }

    return (
        <div>
            <h2>Orders</h2>
            {msg && <div className="alert">{msg}</div>}
            <form onSubmit={makeOrder}>
                <label>Hold id
                    <input value={holdId} onChange={(e) => setHoldId(e.target.value)}/>
                </label>
                <label>User id
                    <input value={userId} onChange={(e) => setUserId(e.target.value)}/>
                </label>
                <label>Order id
                    <input value={orderId} onChange={(e) => setOrderId(e.target.value)}/>
                </label>
                <div className="row">
                    <button type="submit">Create Order</button>
                    <button type="button" onClick={doCancel} disabled={!orderId}>Cancel Order</button>
                    <button type="button" onClick={loadOrder} disabled={!orderId}>Get Order</button>
                    <button type="button" onClick={loadOrders}>List Orders</button>
                </div>
            </form>

            {orders.length > 0 && (
                <div>
                    <h3>Loaded orders</h3>
                    <ul>
                        {orders.map((order) => (
                            <li key={order.orderId}>
                                {order.orderId} — {order.status} — holds: {(order.holdIds || []).join(', ')}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    )
}
