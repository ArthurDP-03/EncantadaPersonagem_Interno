import { useState, useEffect } from "react";
import Swal from "sweetalert2";
import { getPersonagens, criarPersonagem, atualizarPersonagem, deletarPersonagem } from "../services/personagensService";

export function usePersonagens() {
  const [personagens, setPersonagens] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const carregarPersonagens = () => {
    getPersonagens()
      .then(setPersonagens)
      .catch(err => {
        console.error("Erro completo:", err);
        setErro(err.data?.error || err.message || "Erro ao carregar personagens");

        Swal.fire({
          icon: "error",
          title: "Erro ao carregar",
          text: err.data?.error || err.message || "Não foi possível carregar os personagens"
        });
      })
      .finally(() => setCarregando(false));
  };

  useEffect(() => {
    carregarPersonagens();
  }, []);

  const criar = async (personagem) => {
    try {
      await criarPersonagem(personagem);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: "Personagem criado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao criar personagem:", err);

      Swal.fire({
        icon: "error",
        title: "Erro ao criar",
        text: err.data?.error || err.message || "Não foi possível criar o personagem"
      });

      throw err;
    }
  };

  const editar = async (id, personagem) => {
    try {
      await atualizarPersonagem(id, personagem);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: "Personagem atualizado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao editar personagem:", err);

      Swal.fire({
        icon: "error",
        title: "Erro ao atualizar",
        text: err.data?.error || err.message || "Não foi possível atualizar o personagem"
      });

      throw err;
    }
  };

  const deletar = async (id) => {
    const resultado = await Swal.fire({
      title: "Deseja deletar este personagem?",
      text: "Esta ação não poderá ser desfeita.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Deletar",
      cancelButtonText: "Cancelar",
      confirmButtonColor: "#d33"
    });

    if (!resultado.isConfirmed) return;

    try {
      await deletarPersonagem(id);
      carregarPersonagens();

      Swal.fire({
        icon: "success",
        title: "Personagem deletado",
        timer: 1800,
        showConfirmButton: false
      });
    } catch (err) {
      console.error("Erro ao deletar personagem:", err);

      Swal.fire({
        icon: "error",
        title: "Erro ao deletar",
        text: err.data?.message || err.data?.error || "Não foi possível deletar o personagem"
      });
    }
  };

  return { personagens, carregando, erro, criar, editar, deletar };
}