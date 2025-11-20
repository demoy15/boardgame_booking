import React, {useEffect, useState} from 'react'
import {getGames} from '../api'
import HoldForm from './HoldForm'

export default function Games() {
    const [games, setGames] = useState([])
    const [selected, setSelected] = useState(null)
    const [message, setMessage] = useState(null)

    useEffect(() => {
        getGames().then(setGames).catch(err => setMessage(err.message))
    }, [])

    const onHold = (game) => {
        setSelected(game)
    }

    const onHoldCreated = (resp) => {
        setMessage(`Hold created: ${resp.holdId}`)
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
                        <button onClick={() => onHold(g)}>Hold</button>
                    </div>
                ))}
            </div>

            {selected && (
                <HoldForm game={selected} onCreated={onHoldCreated} onCancel={() => setSelected(null)}/>
            )}
        </div>
    )
}
