import React, {useEffect, useState} from 'react'
import {getGames} from '../api'
import HoldForm from './HoldForm'
import {useCart} from '../components/CartContext'

export default function Games() {
    const [games, setGames] = useState([])
    const [selected, setSelected] = useState(null)
    const [message, setMessage] = useState(null)
    const {addItem} = useCart()

    useEffect(() => {
        getGames().then(setGames).catch(err => setMessage(err.message))
    }, [])

    const onHoldCreated = (resp, game) => {
        addItem({game, holdId: resp.holdId, expiresAt: resp.expiresAt})
        setMessage(`Added to cart: ${game.title}`)
        setSelected(null)
    }

    return (
        <div>
            <h2>Games</h2>
            {message && <div className="alert">{message}</div>}
            <div className="grid">
                {games.map(g => (
                    <div className="card" key={g.id}>
                        <h3>{g.title}</h3>
                        <p>Players: {g.minPlayers || '-'}–{g.maxPlayers || '-'}</p>
                        <p>Playtime: {g.playtimeMin || '-'} min</p>
                        <button onClick={() => setSelected(g)}>Add to cart</button>
                    </div>
                ))}
            </div>

            {selected && (
                <HoldForm game={selected} onCreated={(resp) => onHoldCreated(resp, selected)}
                          onCancel={() => setSelected(null)}/>
            )}
        </div>
    )
}
