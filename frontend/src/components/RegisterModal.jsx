import React, {useState} from 'react'
import {createUser} from '../api'

export default function RegisterModal({open, onClose, onRegistered}) {
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)

    if (!open) return null

    const submit = async (e) => {
        e.preventDefault()
        setError(null)
        setLoading(true)
        try {
            const user = await createUser({name, email})
            onRegistered(user)
        } catch (err) {
            setError(err.message)
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="modal">
            <div className="modalCard">
                <h3>Create your profile</h3>
                <p className="muted">Register once to keep your orders and holds in one place.</p>
                {error && <div className="alert">{error}</div>}
                <form onSubmit={submit}>
                    <label>Name
                        <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Your name"/>
                    </label>
                    <label>Email
                        <input value={email} onChange={(e) => setEmail(e.target.value)}
                               placeholder="you@example.com"/>
                    </label>
                    <div className="row">
                        <button type="submit" disabled={loading || !name.trim() || !email.trim()}>
                            Register
                        </button>
                        <button type="button" className="ghost" onClick={onClose}>
                            Close
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}
