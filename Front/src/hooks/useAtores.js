import { useState, useEffect } from "react";
import Swal from "sweetalert2";
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
        setErro(err.data?.error || "Erro ao carregar atores");
        Swal.fire({ icon: "error", title: "Erro ao carregar", text: err.data?.error || "Não foi possível carregar os atores" });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({ title: "Deseja deletar este ator?", text: "Esta ação não poderá ser desfeita.", icon: "warning", showCancelButton: true, confirmButtonText: "Deletar", cancelButtonText: "Cancelar", confirmButtonColor: "#d33" });
    if (!resultado.isConfirmed) return;
    try {
      await deletarAtor(id);
      setAtores(prev => prev.filter(a => a.id !== id));
      Swal.fire({ icon: "success", title: "Ator deletado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar ator:", err);
      Swal.fire({ icon: "error", title: "Erro ao deletar", text: err.data?.message || err.data?.error || "Não foi possível deletar o ator" });
    }
  };

  const criar = async (dados) => {
    try {
      const novoAtor = await criarAtor(dados);
      setAtores(prev => [...prev, novoAtor]);
      Swal.fire({ icon: "success", title: "Ator criado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar ator:", err);
      Swal.fire({ icon: "error", title: "Erro ao criar", text: err.data?.error || "Não foi possível criar o ator" });
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarAtor(id, dados);
      setAtores(prev => prev.map(a => a.id === id ? atualizado : a));
      Swal.fire({ icon: "success", title: "Ator atualizado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar ator:", err);
      Swal.fire({ icon: "error", title: "Erro ao atualizar", text: err.data?.error || "Não foi possível atualizar o ator" });
    }
  };

  return { atores, carregando, erro, deletar, criar, editar };
}