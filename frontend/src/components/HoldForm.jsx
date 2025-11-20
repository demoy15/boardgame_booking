import React, {useState} from 'react'
import {createHold} from '../api'

export default function HoldForm({game, onCreated, onCancel}) {
    const [userId, setUserId] = useState('22222222-2222-2222-2222-222222222222')
    const now = new Date()
    const fromDefault = new Date(now.getTime() + 60 * 60 * 1000) // +1h
    const toDefault = new Date(fromDefault.getTime() + 2 * 60 * 60 * 1000) // +2h
    const [from, setFrom] = useState(fromDefault.toISOString().slice(0, 16))
    const [to, setTo] = useState(toDefault.toISOString().slice(0, 16))
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)


    const submit = async (e) => {
        e.preventDefault()
        setLoading(true)
        try {
            const body = {
                inventoryId: game.id,
                from: new Date(from).toISOString(),
                to: new Date(to).toISOString(),
                userId
            }
            const resp = await createHold(body)
            onCreated(resp)
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }


    return (
        <div className="modal">
            <div className="modalCard">
                <h3>Hold: {game.title}</h3>
                {error && <div className="alert">{error}</div>}
                <form onSubmit={submit}>
                    <label>User id
                        <input value={userId} onChange={(e) => setUserId(e.target.value)}/>
                    </label>
                    <label>From
                        <input type="datetime-local" value={from} onChange={e => setFrom(e.target.value)}/>
                    </label>
                    <label>To
                        <input type="datetime-local" value={to} onChange={e => setTo(e.target.value)}/>
                    </label>

                    <div className="row">
                        <button type="submit" disabled={loading}>Create Hold</button>
                        <button type="button" onClick={onCancel}>Cancel</button>
                    </div>
                </form>
            </div>
        </div>
    )
}
