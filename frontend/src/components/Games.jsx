import React, {useEffect, useMemo, useState} from 'react'
import {createHold, getGames} from '../api'
import {useCart} from './CartContext'
import {getGameImage} from '../gameImages'

function defaultHoldRange() {
    const now = new Date()
    const from = new Date(now.getTime() + 60 * 60 * 1000)
    const to = new Date(from.getTime() + 2 * 60 * 60 * 1000)
    return {from, to}
}

export default function Games({user, onOpenCart}) {
    const [games, setGames] = useState([])
    const [message, setMessage] = useState(null)
    const [query, setQuery] = useState('')
    const [loadingId, setLoadingId] = useState(null)
    const {addItem} = useCart()

    useEffect(() => {
        getGames().then(setGames).catch(err => setMessage(err.message))
    }, [])

    const filtered = useMemo(() => {
        const q = query.trim().toLowerCase()
        if (!q) return games
        return games.filter(g => g.title.toLowerCase().includes(q))
    }, [games, query])

    const addToCart = async (game) => {
        if (!user) {
            setMessage('Please create a profile to book games.')
            return
        }
        setLoadingId(game.id)
        setMessage(null)
        try {
            const {from, to} = defaultHoldRange()
            const resp = await createHold({
                gameId: game.id,
                from: from.toISOString(),
                to: to.toISOString(),
                userId: user.id
            })
            addItem({
                game,
                holdId: resp.holdId,
                expiresAt: resp.expiresAt,
                from: from.toISOString(),
                to: to.toISOString()
            })
            setMessage(`Added to cart: ${game.title}`)
            onOpenCart()
        } catch (err) {
            setMessage(err.message)
        } finally {
            setLoadingId(null)
        }
    }

    const onKeyDown = (event, game) => {
        if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault()
            addToCart(game)
        }
    }

    return (
        <div>
            <section className="hero">
                <div>
                    <h2>Pick a game, reserve it, and confirm in one click.</h2>
                    <p className="muted">
                        Select a title to add it to your cart. Holds are created automatically.
                    </p>
                </div>
                <div className="searchBox">
                    <input
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                        placeholder="Search games..."
                    />
                </div>
            </section>

            {message && <div className="alert">{message}</div>}

            <div className="grid">
                {filtered.map(g => {
                    const imageUrl = getGameImage(g.title)
                    const isLoading = loadingId === g.id
                    return (
                        <div
                            className={`gameCard ${isLoading ? 'isLoading' : ''}`}
                            key={g.id}
                            role="button"
                            tabIndex={0}
                            aria-disabled={isLoading}
                            onClick={() => !isLoading && addToCart(g)}
                            onKeyDown={(e) => onKeyDown(e, g)}
                        >
                            <div className="gameCover">
                                {imageUrl ? (
                                    <img src={imageUrl} alt={g.title}/>
                                ) : (
                                    <div className="coverFallback">
                                        <span>{g.title}</span>
                                    </div>
                                )}
                            </div>
                            <div className="cardHeader">
                                <h3>{g.title}</h3>
                                <span className="pill">Reserve</span>
                            </div>
                            <div className="meta">
                                <span>Players: {g.minPlayers || '-'}-{g.maxPlayers || '-'}</span>
                                <span>Playtime: {g.playtimeMin || '-'} min</span>
                            </div>
                            <div className="cardFooter">
                                {isLoading ? 'Reserving...' : 'Add to cart'}
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}
