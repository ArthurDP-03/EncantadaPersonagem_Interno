import { useState, useEffect } from "react";
import { getAtores, deletarAtor, criarAtor, atualizarAtor } from "../services/atoresService";

export function useAtores() {
  const [atores, setAtores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAtores()
      .then(setAtores)
      .catch(err => {
        console.error("Erro completo:", err);
        setErro(err.message);
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    await deletarAtor(id);
    setAtores(prev => prev.filter(a => a.id !== id));
  };

  const criar = async (dados) => {
    const novoAtor = await criarAtor(dados);
    setAtores(prev => [...prev, novoAtor]);
  };

  const editar = async (id, dados) => {
    const atualizado = await atualizarAtor(id, dados);
    setAtores(prev => prev.map(a => a.id === id ? atualizado : a));
  };


  return { atores, carregando, erro, deletar, criar, editar };
}