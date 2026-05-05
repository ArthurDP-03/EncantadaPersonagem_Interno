import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import {
  getEventos,
  criarEvento,
  atualizarEvento,
  deletarEvento,
} from "../services/eventosService";

export function useEventos() {
  const [eventos, setEventos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getEventos()
      .then(setEventos)
      .catch(err => {
        console.error("Erro completo:", err);
        setErro(err.data?.error || "Erro ao carregar eventos");
        Swal.fire({
          icon: "error",
          title: "Erro ao carregar",
          text: err.data?.error || "Não foi possível carregar os eventos",
        });
      })
      .finally(() => setCarregando(false));
  }, []);

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: "Deseja deletar este evento?",
      text: "Esta ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Deletar",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#d33",
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarEvento(id);
      setEventos(prev => prev.filter(e => e.id !== id));
      Swal.fire({ icon: "success", title: "Evento deletado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao deletar evento:", err);
      Swal.fire({
        icon: "error",
        title: "Erro ao deletar",
        text: err.data?.message || err.data?.error || "Não foi possível deletar o evento",
      });
    }
  };

  const criar = async (dados) => {
    try {
      const novoEvento = await criarEvento(dados);
      setEventos(prev => [...prev, novoEvento]);
      Swal.fire({ icon: "success", title: "Evento criado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao criar evento:", err);
      Swal.fire({
        icon: "error",
        title: "Erro ao criar",
        text: err.data?.error || "Não foi possível criar o evento",
      });
    }
  };

  const editar = async (id, dados) => {
    try {
      const atualizado = await atualizarEvento(id, dados);
      setEventos(prev => prev.map(e => (e.id === id ? atualizado : e)));
      Swal.fire({ icon: "success", title: "Evento atualizado", timer: 1800, showConfirmButton: false });
    } catch (err) {
      console.error("Erro ao editar evento:", err);
      Swal.fire({
        icon: "error",
        title: "Erro ao atualizar",
        text: err.data?.error || "Não foi possível atualizar o evento",
      });
    }
  };

  return { eventos, carregando, erro, criar, editar, deletar };
}