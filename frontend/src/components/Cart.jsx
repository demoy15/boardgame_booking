import React, {useEffect, useState} from 'react'
import {cancelHold, createOrder} from '../api'
import {useCart} from './CartContext'
import {getGameImage} from '../gameImages'

function formatTime(value) {
    if (!value) return '-'
    const d = new Date(value)
    if (Number.isNaN(d.getTime())) return value
    return d.toLocaleString()
}

export default function Cart({open, onClose, user}) {
    const {items, removeItem, clear} = useCart()
    const [msg, setMsg] = useState(null)
    const [loadingId, setLoadingId] = useState(null)
    const [bulkLoading, setBulkLoading] = useState(false)
    const [now, setNow] = useState(Date.now())

    useEffect(() => {
        if (!open) return
        const timer = setInterval(() => setNow(Date.now()), 1000)
        return () => clearInterval(timer)
    }, [open])

    if (!open) return null

    const remove = async (holdId) => {
        setMsg(null)
        setLoadingId(holdId)
        try {
            await cancelHold(holdId)
            removeItem(holdId)
        } catch (e) {
            setMsg(e.message)
        } finally {
            setLoadingId(null)
        }
    }

    const confirmOne = async (holdId) => {
        if (!user) {
            setMsg('Please create a profile first.')
            return
        }
        setMsg(null)
        setLoadingId(holdId)
        try {
            const resp = await createOrder({holdId, userId: user.id})
            setMsg('Order created: ' + resp.orderId)
            removeItem(holdId)
        } catch (e) {
            setMsg('Order failed: ' + e.message)
        } finally {
            setLoadingId(null)
        }
    }

    const confirmAll = async () => {
        if (!user) {
            setMsg('Please create a profile first.')
            return
        }
        if (items.length === 0) return
        setMsg(null)
        setBulkLoading(true)
        try {
            const resp = await createOrder({holdIds: items.map(i => i.holdId), userId: user.id})
            setMsg('Order created: ' + resp.orderId)
            clear()
        } catch (e) {
            setMsg('Order failed: ' + e.message)
        } finally {
            setBulkLoading(false)
        }
    }

    return (
        <div className="cartOverlay" onClick={onClose}>
            <aside className="cartDrawer" onClick={(e) => e.stopPropagation()}>
                <div className="cartHeader">
                    <h3>Your cart</h3>
                    <button className="ghost" onClick={onClose}>Close</button>
                </div>

                {msg && <div className="alert">{msg}</div>}

                {items.length === 0 && (
                    <div className="emptyState">
                        <p>No holds yet.</p>
                        <span className="muted">Pick a game to reserve it instantly.</span>
                    </div>
                )}

                <div className="cartItems">
                    {items.map(it => {
                        const expired = isExpired(it.expiresAt, now)
                        return (
                            <div key={it.holdId} className="cartItem">
                                <div>
                                    <h4>{it.game.title}</h4>
                                    <div className="muted">Hold: {it.holdId}</div>
                                    <div className="muted">Expires: {formatTime(it.expiresAt)}</div>
                                    <div className="timer">
                                        {renderTimer(it.expiresAt, now)}
                                    </div>
                                </div>
                                <div className="cartThumb">
                                    {getGameImage(it.game.title) ? (
                                        <img src={getGameImage(it.game.title)} alt={it.game.title}/>
                                    ) : (
                                        <div className="coverFallback small">
                                            <span>{it.game.title}</span>
                                        </div>
                                    )}
                                </div>
                                <div className="cartActions">
                                    <button
                                        onClick={() => confirmOne(it.holdId)}
                                        disabled={loadingId === it.holdId || expired}
                                    >
                                        Confirm
                                    </button>
                                    <button
                                        className="ghost"
                                        onClick={() => remove(it.holdId)}
                                        disabled={loadingId === it.holdId}
                                    >
                                        Remove
                                    </button>
                                </div>
                            </div>
                        )
                    })}
                </div>

                <div className="cartFooter">
                    <button onClick={confirmAll} disabled={bulkLoading || items.length === 0}>
                        Confirm all
                    </button>
                </div>
            </aside>
        </div>
    )
}

function renderTimer(expiresAt, now) {
    if (!expiresAt) return 'Timer not available'
    const end = new Date(expiresAt).getTime()
    if (Number.isNaN(end)) return 'Timer not available'
    const diff = end - now
    if (diff <= 0) return 'Expired'
    const totalSeconds = Math.floor(diff / 1000)
    const minutes = Math.floor(totalSeconds / 60)
    const seconds = totalSeconds % 60
    return `Time left: ${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

function isExpired(expiresAt, now) {
    if (!expiresAt) return false
    const end = new Date(expiresAt).getTime()
    if (Number.isNaN(end)) return false
    return end - now <= 0
}
