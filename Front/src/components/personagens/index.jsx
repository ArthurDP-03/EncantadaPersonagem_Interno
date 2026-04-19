// components/Personagens.jsx
import { usePersonagens } from "../../hooks/usePersonagem";

function Personagens() {
  const { personagens, carregando, erro } = usePersonagens();

  if (carregando) return <p>Carregando...</p>;
  if (erro) return <p>Erro: {erro}</p>;

  return (
    <section className="section-personagens">
      <div className="conteudo-90">
        <h1>Personagens</h1>
        <ul>
          {personagens.map(personagem => (
            <li key={personagem.id}>
              {personagem.nome}
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}

export default Personagens;