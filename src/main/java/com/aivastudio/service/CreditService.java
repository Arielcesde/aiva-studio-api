package com.aivastudio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aivastudio.exception.RecursoNoEncontradoException;
import com.aivastudio.exception.ReglaDeNegocioException;
import com.aivastudio.model.dto.CreditMovementResponse;
import com.aivastudio.model.dto.CreditRechargeRequest;
import com.aivastudio.model.dto.CreditRechargeResponse;
import com.aivastudio.model.dto.UserResponse;
import com.aivastudio.model.entity.Credit;
import com.aivastudio.model.entity.User;
import com.aivastudio.repository.CreditRepository;
import com.aivastudio.repository.UserRepository;


@Service
public class CreditService {

    private static final long MINIMUM_RECHARGE = 50;
    private static final long BONUS_THRESHOLD = 500;
    private static final long BONUS_PERCENT = 20;

    private final UserService currentUserService;
    private final UserRepository userRepository;
    private final CreditRepository creditRepository;

    public CreditService(
            UserService currentUserService,
            UserRepository userRepository,
            CreditRepository creditRepository) {
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
        this.creditRepository = creditRepository;
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(long userId) {
        return UserResponse.from(currentUserService.requireUser(userId));
    }

    @Transactional(readOnly = true)
    public List<CreditMovementResponse> listMovements(long userId) {
        currentUserService.requireUser(userId);
        return creditRepository.findAllByUser_IdOrderByFechaCreacionDesc(userId)
                .stream()
                .map(CreditMovementResponse::from)
                .toList();
    }

    @Transactional
    public CreditRechargeResponse recharge(long userId, CreditRechargeRequest request) {
        currentUserService.requireUser(userId);
        User user = userRepository.findLockedById(userId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Long amount = request.cantidad();
        if (amount < MINIMUM_RECHARGE) {
            throw new ReglaDeNegocioException("La recarga mínima es de 50 créditos");
        }

        long bonus;
        long total;
        long updatedCredits;
        try {
            bonus = amount >= BONUS_THRESHOLD
                    ? Math.floorDiv(Math.multiplyExact(amount, BONUS_PERCENT), 100)
                    : 0;
            total = Math.addExact(amount, bonus);
            updatedCredits = Math.addExact(user.getCredits(), total);
        } catch (ArithmeticException ex) {
            throw new ReglaDeNegocioException("La recarga supera el saldo máximo permitido");
        }
        user.setCredits(updatedCredits);
        Credit credit = creditRepository.save(new Credit(
                user,
                total,
                bonus > 0 ? "Recarga de prueba (incluye bono)" : "Recarga de prueba"));

        return new CreditRechargeResponse(
                credit.getId(),
                credit.getDescription(),
                credit.getAmount(),
                credit.getFechaCreacion(),
                user.getId(),
                amount,
                bonus,
                total,
                updatedCredits);
    }

    @Transactional(readOnly = true)
    public long totalCredited(long userId) {
        currentUserService.requireUser(userId);
        return creditRepository.totalAcreditadoPorUsuario(userId);
    }
}
