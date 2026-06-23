import "./index.css";
import { ArrowLeft } from "lucide-react";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Swal from "sweetalert2";
import { listarMeusConvites } from "../../services/conviteService";
import { getMinhasEscalacoes } from "../../services/escalacaoService";
import { getEventoById } from "../../services/eventosService";
import { formatarPeriodoEvento, formatarStatus } from "../../utils/formatters";

const obterMensagemErro = (err, fallback) => err.data?.message || err.data?.error || fallback;

function EventoAtorDetalhes() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [evento, setEvento] = useState(null);
  const [convitesEvento, setConvitesEvento] = useState([]);
  const [escalacoesEvento, setEscalacoesEvento] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    async function carregarEvento() {
      try {
        setCarregando(true);
        setErro(null);
        const eventoId = Number(id);
        const [eventoAtual, minhasEscalacoes, meusConvites] = await Promise.all([
          getEventoById(eventoId),
          getMinhasEscalacoes(),
          listarMeusConvites(),
        ]);

        setEvento(eventoAtual);
        setEscalacoesEvento(minhasEscalacoes.filter((escala) => escala.eventoId === eventoId));
        setConvitesEvento(meusConvites.filter((convite) => convite.eventoId === eventoId));
      } catch (err) {
        console.error("Erro ao carregar evento do ator:", err);
        const mensagem = obterMensagemErro(err, "Você não possui acesso a este evento");
        setErro(mensagem);
        Swal.fire({
          icon: "error",
          title: "Não foi possível abrir o evento",
          text: mensagem,
        });
      } finally {
        setCarregando(false);
      }
    }

    carregarEvento();
  }, [id]);

  if (carregando) {
    return <main className="evento-ator-page">Carregando evento...</main>;
  }

  if (erro || !evento) {
    return (
      <main className="evento-ator-page">
        <button className="evento-ator-back" type="button" onClick={() => navigate("/eventos")}>
          <ArrowLeft size={18} />
          Voltar para eventos
        </button>
        <p className="evento-ator-error">{erro || "Evento não encontrado"}</p>
      </main>
    );
  }

  return (
    <main className="evento-ator-page">
      <button className="evento-ator-back" type="button" onClick={() => navigate("/eventos")}>
        <ArrowLeft size={18} />
        Voltar para eventos
      </button>

      <header className="evento-ator-header">
        <span>Evento #{evento.id}</span>
        <h1>{evento.titulo}</h1>
        <p>{formatarStatus(evento.status)}</p>
      </header>

      <section className="evento-ator-panel">
        <h2>Informações do evento</h2>
        <dl>
          <div>
            <dt>Período</dt>
            <dd>{formatarPeriodoEvento(evento.dataInicio, evento.dataFim)}</dd>
          </div>
          <div>
            <dt>Endereço</dt>
            <dd>{evento.endereco}</dd>
          </div>
          <div>
            <dt>Descrição</dt>
            <dd>{evento.descricao || "Sem descrição informada."}</dd>
          </div>
        </dl>
      </section>

      <section className="evento-ator-panel">
        <h2>Sua participação</h2>
        <div className="evento-ator-personagens">
          {escalacoesEvento.length > 0 ? (
            escalacoesEvento.map((escala) => (
              <article key={escala.id}>
                <span className="evento-ator-kicker">Personagem escalado</span>
                <h3>{escala.personagemNome}</h3>
                <p>Item {escala.personagemItemCodigo}</p>
                <small>{escala.status.replaceAll("_", " ")}</small>
              </article>
            ))
          ) : convitesEvento.length > 0 ? (
            convitesEvento.map((convite) => (
              <article key={convite.id}>
                <span className="evento-ator-kicker">Convite</span>
                <h3>{convite.personagemNome}</h3>
                <p>Item {convite.personagemItemCodigo}</p>
                <small>{convite.status}</small>
              </article>
            ))
          ) : (
            <p className="evento-ator-empty">Nenhum personagem vinculado ao seu usuário neste evento.</p>
          )}
        </div>
      </section>
    </main>
  );
}

export default EventoAtorDetalhes;
