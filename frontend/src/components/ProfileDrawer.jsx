import React, {useEffect, useState} from 'react'
import {listOrders} from '../api'

function formatDate(value) {
    if (!value) return '-'
    const d = new Date(value)
    if (Number.isNaN(d.getTime())) return value
    return d.toLocaleString()
}

export default function ProfileDrawer({open, onClose, user, onSignOut}) {
    const [orders, setOrders] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (!open || !user) return
        setLoading(true)
        setError(null)
        listOrders({userId: user.id})
            .then((items) => setOrders(items || []))
            .catch((err) => setError(err.message))
            .finally(() => setLoading(false))
    }, [open, user?.id])

    if (!open) return null

    return (
        <div className="cartOverlay" onClick={onClose}>
            <aside className="cartDrawer" onClick={(e) => e.stopPropagation()}>
                <div className="cartHeader">
                    <h3>Profile</h3>
                    <button className="ghost" onClick={onClose}>Close</button>
                </div>

                <div className="profileCard">
                    <div className="profileName">{user?.name || 'Guest'}</div>
                    <div className="muted">{user?.email || 'No email'}</div>
                </div>

                <div className="sectionTitle">Your orders</div>
                {loading && <div className="muted">Loading orders...</div>}
                {error && <div className="alert">{error}</div>}

                {(!loading && orders.length === 0) && (
                    <div className="emptyState">
                        <p>No orders yet.</p>
                        <span className="muted">Confirm a booking to see it here.</span>
                    </div>
                )}

                <div className="cartItems">
                    {orders.map((order) => (
                        <div key={order.orderId} className="cartItem">
                            <div>
                                <h4>{order.orderId}</h4>
                                <div className="muted">Status: {order.status}</div>
                                <div className="muted">Created: {formatDate(order.createdAt)}</div>
                            </div>
                            <div className="muted">
                                Holds: {(order.holdIds || []).length}
                            </div>
                        </div>
                    ))}
                </div>

                <div className="cartFooter">
                    <button className="ghost" onClick={onSignOut}>Sign out</button>
                </div>
            </aside>
        </div>
    )
}
