import { useMemo, useState } from 'react'
import './index.css'

const endpointGroups = [
    {
        entity: 'Autenticacao',
        actions: [
            {
                label: 'Login de administrador',
                method: 'POST',
                path: '/auth/login',
                description: 'Autentica um administrador e retorna o token JWT.',
                body: {
                    email: 'admin@encantada.com',
                    senha: 'Senha123!'
                }
            }
        ]
    },
    {
        entity: 'Administrador',
        actions: [
            {
                label: 'Listar administradores',
                method: 'GET',
                path: '/administradores',
                description: 'Busca os administradores cadastrados.',
                body: null
            },
            {
                label: 'Criar administrador',
                method: 'POST',
                path: '/administradores',
                description: 'Cria um novo administrador.',
                body: {
                    nome: 'Ana Martins',
                    email: 'ana@encantada.com',
                    senha: 'Senha123!',
                    telefone: '(11) 99999-0000',
                    tipo: 'ADMIN'
                }
            }
        ]
    },
    {
        entity: 'Ator',
        actions: [
            {
                label: 'Listar atores',
                method: 'GET',
                path: '/atores',
                description: 'Lista atores ativos e inativos.',
                body: null
            },
            {
                label: 'Criar ator',
                method: 'POST',
                path: '/atores',
                description: 'Cadastra um novo ator.',
                body: {
                    nome: 'Pedro Lima',
                    email: 'pedro@encantada.com',
                    telefone: '(11) 98888-1111',
                    genero: 'Masculino',
                    altura: 1.8,
                    peso: 78,
                    observacao: 'Disponivel aos fins de semana',
                    ativo: true
                }
            }
        ]
    },
    {
        entity: 'Cliente',
        actions: [
            {
                label: 'Listar clientes',
                method: 'GET',
                path: '/clientes',
                description: 'Retorna os clientes cadastrados.',
                body: null
            },
            {
                label: 'Criar cliente',
                method: 'POST',
                path: '/clientes',
                description: 'Cadastra um novo cliente.',
                body: {
                    nome: 'Carla Souza',
                    telefone: '(11) 97777-2222',
                    email: 'carla@cliente.com'
                }
            }
        ]
    },
    {
        entity: 'Personagem',
        actions: [
            {
                label: 'Listar personagens',
                method: 'GET',
                path: '/personagens',
                description: 'Lista os tipos de personagem.',
                body: null
            },
            {
                label: 'Criar personagem',
                method: 'POST',
                path: '/personagens',
                description: 'Cria um novo personagem.',
                body: {
                    nome: 'Princesa Aurora',
                    descricao: 'Personagem para festas infantis',
                    foto: 'https://exemplo.com/imagens/aurora.png'
                }
            }
        ]
    },
    {
        entity: 'PersonagemItem',
        actions: [
            {
                label: 'Listar itens de personagem',
                method: 'GET',
                path: '/personagem-itens',
                description: 'Consulta o estoque fisico dos personagens.',
                body: null
            },
            {
                label: 'Criar item de personagem',
                method: 'POST',
                path: '/personagem-itens',
                description: 'Cadastra um item fisico de um personagem.',
                body: {
                    personagemId: 1,
                    codigo: 'PRINCESA-001',
                    status: 'DISPONIVEL'
                }
            }
        ]
    },
    {
        entity: 'Evento',
        actions: [
            {
                label: 'Listar eventos',
                method: 'GET',
                path: '/eventos',
                description: 'Lista os eventos cadastrados.',
                body: null
            },
            {
                label: 'Criar evento',
                method: 'POST',
                path: '/eventos',
                description: 'Cria um evento associado a um cliente.',
                body: {
                    titulo: 'Aniversario da Maria',
                    descricao: 'Festa com personagem principal',
                    dataInicio: '2026-04-18T14:00:00',
                    dataFim: '2026-04-18T18:00:00',
                    endereco: 'Rua das Flores, 100',
                    status: 'CONFIRMADO',
                    tipoPagamento: 'PIX',
                    valorTotal: 850,
                    clienteId: 1
                }
            }
        ]
    },
    {
        entity: 'EventoPersonagem',
        actions: [
            {
                label: 'Listar vinculos evento-personagem',
                method: 'GET',
                path: '/evento-personagens',
                description: 'Consulta personagens associados a eventos.',
                body: null
            },
            {
                label: 'Criar vinculo evento-personagem',
                method: 'POST',
                path: '/evento-personagens',
                description: 'Associa um personagem a um evento.',
                body: {
                    eventoId: 1,
                    personagemId: 1
                }
            }
        ]
    },
    {
        entity: 'Convite',
        actions: [
            {
                label: 'Listar convites',
                method: 'GET',
                path: '/convites',
                description: 'Lista os convites enviados aos atores.',
                body: null
            },
            {
                label: 'Criar convite',
                method: 'POST',
                path: '/convites',
                description: 'Envia um convite para um ator.',
                body: {
                    eventoPersonagemId: 1,
                    atorId: 1
                }
            },
            {
                label: 'Responder convite',
                method: 'PATCH',
                path: '/convites/1/resposta',
                description: 'Atualiza o status do convite.',
                body: {
                    status: 'ACEITO'
                }
            }
        ]
    },
    {
        entity: 'Escalacao',
        actions: [
            {
                label: 'Listar escalacoes',
                method: 'GET',
                path: '/escalacoes',
                description: 'Consulta as definicoes finais de ator e item.',
                body: null
            },
            {
                label: 'Criar escalacao',
                method: 'POST',
                path: '/escalacoes',
                description: 'Define ator e item final para o evento.',
                body: {
                    eventoPersonagemId: 1,
                    atorId: 1,
                    personagemItemId: 1
                }
            }
        ]
    }
]

const initialAction = endpointGroups[0].actions[0]

function formatBody(body) {
    return body ? JSON.stringify(body, null, 2) : ''
}

function Demo() {
    const [baseUrl, setBaseUrl] = useState('http://localhost:8080/api')
    const [token, setToken] = useState('')
    const [method, setMethod] = useState(initialAction.method)
    const [path, setPath] = useState(initialAction.path)
    const [body, setBody] = useState(formatBody(initialAction.body))
    const [selectedActionLabel, setSelectedActionLabel] = useState(initialAction.label)
    const [responseData, setResponseData] = useState(null)
    const [statusText, setStatusText] = useState('Nenhuma requisicao executada ainda.')
    const [isLoading, setIsLoading] = useState(false)
    const [errorText, setErrorText] = useState('')

    const flattenedActions = useMemo(
        () => endpointGroups.flatMap((group) => group.actions.map((action) => ({ ...action, entity: group.entity }))),
        []
    )

    function selectAction(action) {
        setSelectedActionLabel(action.label)
        setMethod(action.method)
        setPath(action.path)
        setBody(formatBody(action.body))
        setErrorText('')
    }

    async function handleSubmit(event) {
        event.preventDefault()
        setIsLoading(true)
        setErrorText('')

        try {
            let parsedBody
            if (body.trim()) {
                parsedBody = JSON.parse(body)
            }

            const headers = {
                'Content-Type': 'application/json'
            }

            if (token.trim()) {
                headers.Authorization = `Bearer ${token.trim()}`
            }

            const startedAt = performance.now()
            const response = await fetch(`${baseUrl.replace(/\/$/, '')}${path}`, {
                method,
                headers,
                body: ['GET', 'DELETE'].includes(method) ? undefined : JSON.stringify(parsedBody)
            })
            const elapsed = Math.round(performance.now() - startedAt)
            const text = await response.text()

            let parsedResponse
            try {
                parsedResponse = text ? JSON.parse(text) : null
            } catch {
                parsedResponse = text
            }

            if (path === '/auth/login' && parsedResponse?.token) {
                setToken(parsedResponse.token)
            }

            setResponseData(parsedResponse)
            setStatusText(`${response.status} ${response.statusText} em ${elapsed} ms`)
        } catch (error) {
            setResponseData(null)
            setStatusText('Falha ao executar requisicao.')
            setErrorText(error instanceof Error ? error.message : 'Erro desconhecido')
        } finally {
            setIsLoading(false)
        }
    }

    return (
        <section className="demo-page">
            <div className="demo-shell">
                <header className="demo-hero">
                    <div>
                        <p className="demo-overline">Painel de integracao</p>
                        <h1 className="demo-title">Demo</h1>
                        <p className="demo-subtitle">
                            Use esta tela para disparar requests contra o backend, testar todas as entidades e visualizar a
                            resposta em tempo real.
                        </p>
                    </div>
                    <div className="demo-status-card">
                        <span className="demo-status-label">Ultimo resultado</span>
                        <strong>{statusText}</strong>
                        {errorText ? <span className="demo-error-inline">{errorText}</span> : null}
                    </div>
                </header>

                <div className="demo-layout">
                    <aside className="demo-sidebar">
                        <div className="demo-panel sticky">
                            <h2>Configuracao</h2>
                            <label className="demo-field">
                                <span>Base da API</span>
                                <input value={baseUrl} onChange={(event) => setBaseUrl(event.target.value)} />
                            </label>
                            <label className="demo-field">
                                <span>JWT</span>
                                <textarea
                                    value={token}
                                    onChange={(event) => setToken(event.target.value)}
                                    rows={5}
                                    placeholder="Cole aqui o token JWT"
                                />
                            </label>
                        </div>

                        <div className="demo-panel">
                            <h2>Presets por entidade</h2>
                            <div className="demo-groups">
                                {endpointGroups.map((group) => (
                                    <section key={group.entity} className="demo-group">
                                        <h3>{group.entity}</h3>
                                        <div className="demo-action-list">
                                            {group.actions.map((action) => (
                                                <button
                                                    key={action.label}
                                                    type="button"
                                                    className={selectedActionLabel === action.label ? 'demo-action active' : 'demo-action'}
                                                    onClick={() => selectAction(action)}
                                                >
                                                    <span>{action.label}</span>
                                                    <small>{action.method} {action.path}</small>
                                                </button>
                                            ))}
                                        </div>
                                    </section>
                                ))}
                            </div>
                        </div>
                    </aside>

                    <main className="demo-main">
                        <div className="demo-panel">
                            <h2>Request Builder</h2>
                            <form className="demo-form" onSubmit={handleSubmit}>
                                <div className="demo-row compact">
                                    <label className="demo-field method-field">
                                        <span>Metodo</span>
                                        <select value={method} onChange={(event) => setMethod(event.target.value)}>
                                            <option value="GET">GET</option>
                                            <option value="POST">POST</option>
                                            <option value="PUT">PUT</option>
                                            <option value="PATCH">PATCH</option>
                                            <option value="DELETE">DELETE</option>
                                        </select>
                                    </label>
                                    <label className="demo-field grow">
                                        <span>Path</span>
                                        <input value={path} onChange={(event) => setPath(event.target.value)} />
                                    </label>
                                </div>

                                <label className="demo-field">
                                    <span>Body JSON</span>
                                    <textarea
                                        value={body}
                                        onChange={(event) => setBody(event.target.value)}
                                        rows={16}
                                        placeholder={"{\n  \"campo\": \"valor\"\n}"}
                                    />
                                </label>

                                <div className="demo-form-actions">
                                    <button type="submit" className="demo-submit" disabled={isLoading}>
                                        {isLoading ? 'Executando...' : 'Executar requisicao'}
                                    </button>
                                    <button type="button" className="demo-secondary" onClick={() => setBody('')}>
                                        Limpar body
                                    </button>
                                </div>
                            </form>
                        </div>

                        <div className="demo-panel">
                            <h2>Guia rapido</h2>
                            <div className="demo-help-grid">
                                <article>
                                    <h3>1. Criar administrador</h3>
                                    <p>Use o preset de criacao para registrar o primeiro usuario com senha criptografada.</p>
                                </article>
                                <article>
                                    <h3>2. Fazer login</h3>
                                    <p>Execute o preset de login. Se der certo, o token entra automaticamente no campo JWT.</p>
                                </article>
                                <article>
                                    <h3>3. Testar modulos</h3>
                                    <p>Depois do login, rode os presets protegidos para atores, clientes, personagens e eventos.</p>
                                </article>
                                <article>
                                    <h3>4. Ler a resposta</h3>
                                    <p>O painel abaixo mostra status HTTP, payload retornado e mensagens de erro do backend.</p>
                                </article>
                            </div>
                        </div>

                        <div className="demo-panel response-panel">
                            <div className="response-header">
                                <h2>Resposta</h2>
                                <span>{statusText}</span>
                            </div>
                            <pre>
                                {responseData === null
                                    ? 'A resposta do backend aparecera aqui.'
                                    : JSON.stringify(responseData, null, 2)}
                            </pre>
                        </div>

                        <div className="demo-panel">
                            <h2>Mapa de cobertura</h2>
                            <div className="demo-coverage">
                                {flattenedActions.map((action) => (
                                    <div key={`${action.entity}-${action.label}`} className="demo-coverage-item">
                                        <strong>{action.entity}</strong>
                                        <span>{action.label}</span>
                                        <small>{action.method} {action.path}</small>
                                        <p>{action.description}</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </main>
                </div>
            </div>
        </section>
    )
}

export default Demo
