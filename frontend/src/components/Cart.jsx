import React from 'react'
import {useCart} from '../components/CartContext'
import {cancelHold, createOrder} from '../api'

export default function Cart() {
    const {items, removeItem, clear} = useCart()
    const [msg, setMsg] = React.useState(null)
    const [loading, setLoading] = React.useState(false)
    const userId = '22222222-2222-2222-2222-222222222222'

    const remove = async (holdId) => {
        try {
            await cancelHold(holdId)
            removeItem(holdId)
        } catch (e) {
            setMsg(e.message)
        }
    }

    const checkout = async () => {
        if (items.length === 0) return
        setLoading(true)
        try {
            const resp = await createOrder({holdIds: items.map(i => i.holdId), userId})
            setMsg('Order created: ' + resp.orderId)
            clear()
        } catch (e) {
            setMsg('Order failed: ' + e.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div>
            <h2>Cart</h2>
            {msg && <div className="alert">{msg}</div>}
            {items.length === 0 && <p>Cart is empty</p>}
            <div>
                {items.map(it => (
                    <div key={it.holdId} className="card">
                        <h4>{it.game.title}</h4>
                        <p>Hold: {it.holdId}</p>
                        <p>Expires: {it.expiresAt}</p>
                        <button onClick={() => remove(it.holdId)}>Remove</button>
                    </div>
                ))}
            </div>

            <div className="row">
                <button onClick={checkout} disabled={loading || items.length === 0}>Checkout</button>
            </div>
        </div>
    )
}
